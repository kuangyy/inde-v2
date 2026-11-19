use crate::auth::{
    self, create_session, delete_session, login_failed, login_locked, login_succeeded,
    session_cookie, verify_password, COOKIE_PLAYER, KIND_PLAYER,
};
use crate::error::Result;
use crate::game::{self, Script, STATUS_FINISHED, STATUS_IN, STATUS_INTERRUPTED};
use crate::models::{DramaRow, PlayerRow, PlayerView, ProgressRow};
use crate::state::SharedState;
use crate::util::now_ts;
use crate::web::{build_ctx, check_csrf, redirect_msg, HtmlTemplate};
use askama::Template;
use axum::extract::{ConnectInfo, Form, Path, Query, State};
use axum::http::StatusCode;
use axum::response::{IntoResponse, Redirect, Response};
use axum_extra::extract::cookie::CookieJar;
use serde::Deserialize;
use std::collections::HashMap;
use std::net::SocketAddr;

const PLAYER_SESSION_TTL: i64 = 30 * 24 * 3600;

#[derive(Clone)]
pub struct ChoiceItem {
    pub index: i64,
    pub description: String,
}

#[derive(Clone, Default)]
pub struct CurrentGame {
    pub has: bool,
    pub drama_id: i64,
    pub drama_name: String,
    pub drama_desc: String,
    pub node_desc: String,
    pub finished: bool,
    pub choices: Vec<ChoiceItem>,
}

#[derive(Clone)]
pub struct DramaCard {
    pub id: i64,
    pub name: String,
    pub description: String,
    pub status_note: String,
}

#[derive(Template)]
#[template(path = "game_play.html")]
struct PlayTemplate {
    ctx: crate::models::Ctx,
    player: PlayerView,
    dramas: Vec<DramaCard>,
    current: CurrentGame,
    notice: String,
    pending_days: i64,
    can_signin: bool,
}

async fn load_player(state: &SharedState, id: i64) -> Result<Option<PlayerRow>> {
    let player = sqlx::query_as::<_, PlayerRow>(
        "SELECT id, username, password_hash, display_name, coins, points, level, \
                last_signin_at, created_at, updated_at FROM players WHERE id = ?",
    )
    .bind(id)
    .fetch_optional(&state.db)
    .await?;
    Ok(player)
}

async fn load_progress(state: &SharedState, player_id: i64) -> Result<Vec<ProgressRow>> {
    let rows = sqlx::query_as::<_, ProgressRow>(
        "SELECT player_id, drama_id, node_id, status, updated_at FROM game_progress \
         WHERE player_id = ? ORDER BY updated_at DESC",
    )
    .bind(player_id)
    .fetch_all(&state.db)
    .await?;
    Ok(rows)
}

fn progress_note(progress: Option<&ProgressRow>) -> String {
    match progress {
        None => "未开始".to_string(),
        Some(p) => game::status_label(p.status).to_string(),
    }
}

async fn build_current(
    _state: &SharedState,
    dramas: &[DramaRow],
    progress: &[ProgressRow],
) -> CurrentGame {
    let Some(active) = progress
        .iter()
        .find(|p| p.status == STATUS_IN)
        .or_else(|| progress.iter().find(|p| p.status == STATUS_FINISHED))
    else {
        return CurrentGame::default();
    };
    let Some(drama) = dramas.iter().find(|d| d.id == active.drama_id) else {
        return CurrentGame::default();
    };
    let mut current = CurrentGame {
        has: true,
        drama_id: drama.id,
        drama_name: drama.name.clone(),
        drama_desc: drama.description.clone(),
        node_desc: String::new(),
        finished: active.status == STATUS_FINISHED,
        choices: Vec::new(),
    };
    match Script::parse(&drama.script_json) {
        Ok(script) => match script.node(&active.node_id) {
            Some(node) => {
                current.node_desc = node.description.clone();
                current.choices = node
                    .choices
                    .iter()
                    .enumerate()
                    .map(|(i, c)| ChoiceItem {
                        index: (i + 1) as i64,
                        description: c.description.clone(),
                    })
                    .collect();
            }
            None => current.node_desc = "剧本节点缺失，请联系管理员。".to_string(),
        },
        Err(_) => current.node_desc = "剧本数据损坏，请联系管理员。".to_string(),
    }
    current
}

pub async fn play(
    State(state): State<SharedState>,
    jar: CookieJar,
    Query(query): Query<HashMap<String, String>>,
) -> Result<Response> {
    let Some(session) = auth::player_session(&state, &jar).await else {
        return Ok(Redirect::to("/game/login").into_response());
    };
    let Some(player) = load_player(&state, session.subject_id).await? else {
        return Ok((
            jar.add(auth::clear_cookie(&state, COOKIE_PLAYER)),
            Redirect::to("/game/login"),
        )
            .into_response());
    };
    let ctx = build_ctx(&state, &jar).await;
    let progress = load_progress(&state, player.id).await?;
    let dramas: Vec<DramaRow> = sqlx::query_as::<_, DramaRow>(
        "SELECT id, name, description, status, script_json, created_at, updated_at \
         FROM dramas ORDER BY id ASC",
    )
    .fetch_all(&state.db)
    .await?;

    let cards: Vec<DramaCard> = dramas
        .iter()
        .filter(|d| d.status == 1)
        .map(|d| {
            let note = progress_note(progress.iter().find(|p| p.drama_id == d.id));
            DramaCard {
                id: d.id,
                name: d.name.clone(),
                description: d.description.clone(),
                status_note: note,
            }
        })
        .collect();

    let current = build_current(&state, &dramas, &progress).await;
    let pending_days = game::elapsed_days(player.last_signin_at, player.created_at, now_ts());
    let notice = query.get("msg").cloned().unwrap_or_default();
    let player_view = PlayerView::from(&player);
    Ok(HtmlTemplate(PlayTemplate {
        ctx,
        player: player_view,
        dramas: cards,
        current,
        notice,
        pending_days,
        can_signin: pending_days > 0,
    })
    .into_response())
}

#[derive(Deserialize)]
pub struct CsrfForm {
    #[serde(default)]
    pub csrf: String,
}

async fn action_context(
    state: &SharedState,
    jar: &CookieJar,
    csrf: &str,
) -> Result<std::result::Result<(auth::Session, PlayerRow), Response>> {
    let Some(session) = auth::player_session(state, jar).await else {
        return Ok(Err(Redirect::to("/game/login").into_response()));
    };
    check_csrf(&session, csrf)?;
    let Some(player) = load_player(state, session.subject_id).await? else {
        return Ok(Err(Redirect::to("/game/login").into_response()));
    };
    Ok(Ok((session, player)))
}

pub async fn signin(
    State(state): State<SharedState>,
    jar: CookieJar,
    Form(form): Form<CsrfForm>,
) -> Result<Response> {
    let Ok((_session, player)) = action_context(&state, &jar, &form.csrf).await? else {
        return Ok(Redirect::to("/game/login").into_response());
    };
    let now = now_ts();
    let days = if player.last_signin_at <= 0 {
        1
    } else {
        game::elapsed_days(player.last_signin_at, player.created_at, now)
    };
    if days <= 0 {
        return Ok(redirect_msg(
            "/game/play",
            "今日已签到，游戏日每天 07:00 后可再次签到。",
        ));
    }
    let coins = game::COINS_PER_DAY.saturating_mul(days);
    let points = game::POINTS_PER_DAY.saturating_mul(days);
    sqlx::query(
        "UPDATE players SET coins = coins + ?, points = points + ?, last_signin_at = ?, updated_at = ? WHERE id = ?",
    )
    .bind(coins)
    .bind(points)
    .bind(now)
    .bind(now)
    .bind(player.id)
    .execute(&state.db)
    .await?;
    Ok(redirect_msg(
        "/game/play",
        &format!("签到成功，获得 {coins} 金币、{points} 积分（共 {days} 个游戏日）。"),
    ))
}

#[derive(Deserialize)]
pub struct RenameForm {
    #[serde(default)]
    pub csrf: String,
    #[serde(default)]
    pub name: String,
}

pub async fn rename(
    State(state): State<SharedState>,
    jar: CookieJar,
    Form(form): Form<RenameForm>,
) -> Result<Response> {
    let Ok((_session, player)) = action_context(&state, &jar, &form.csrf).await? else {
        return Ok(Redirect::to("/game/login").into_response());
    };
    let name = form.name.trim();
    if name.is_empty() {
        return Ok(redirect_msg("/game/play", "昵称不能为空。"));
    }
    if name.chars().count() > 50 {
        return Ok(redirect_msg("/game/play", "字数太长，50 字以内。"));
    }
    if player.display_name == name {
        return Ok(redirect_msg("/game/play", "昵称未发生改变。"));
    }
    let exists: i64 =
        sqlx::query_scalar("SELECT COUNT(*) FROM players WHERE display_name = ? AND id <> ?")
            .bind(name)
            .bind(player.id)
            .fetch_one(&state.db)
            .await?;
    if exists > 0 {
        return Ok(redirect_msg("/game/play", "昵称已存在，请换一个~"));
    }
    sqlx::query("UPDATE players SET display_name = ?, updated_at = ? WHERE id = ?")
        .bind(name)
        .bind(now_ts())
        .bind(player.id)
        .execute(&state.db)
        .await?;
    Ok(redirect_msg("/game/play", "修改成功。"))
}

pub async fn levelup(
    State(state): State<SharedState>,
    jar: CookieJar,
    Form(form): Form<CsrfForm>,
) -> Result<Response> {
    let Ok((_session, player)) = action_context(&state, &jar, &form.csrf).await? else {
        return Ok(Redirect::to("/game/login").into_response());
    };
    let level = player.level;
    if player.points < level {
        return Ok(redirect_msg(
            "/game/play",
            &format!("升级失败，没有足够的积分（{}/{}）。", player.points, level),
        ));
    }
    let new_points = player.points - level;
    let new_level = level + 1;
    sqlx::query("UPDATE players SET points = ?, level = ?, updated_at = ? WHERE id = ?")
        .bind(new_points)
        .bind(new_level)
        .bind(now_ts())
        .bind(player.id)
        .execute(&state.db)
        .await?;
    Ok(redirect_msg(
        "/game/play",
        &format!("升级成功，当前 Lv {new_level}，积分剩余：{new_points}。"),
    ))
}

async fn fetch_drama(state: &SharedState, id: i64) -> Result<Option<DramaRow>> {
    let drama = sqlx::query_as::<_, DramaRow>(
        "SELECT id, name, description, status, script_json, created_at, updated_at FROM dramas WHERE id = ?",
    )
    .bind(id)
    .fetch_optional(&state.db)
    .await?;
    Ok(drama)
}

async fn other_active_drama(
    state: &SharedState,
    player_id: i64,
    except_drama_id: i64,
) -> Result<Option<String>> {
    let row: Option<(String,)> = sqlx::query_as(
        "SELECT d.name FROM game_progress p JOIN dramas d ON d.id = p.drama_id \
         WHERE p.player_id = ? AND p.status = ? AND p.drama_id <> ? LIMIT 1",
    )
    .bind(player_id)
    .bind(STATUS_IN)
    .bind(except_drama_id)
    .fetch_optional(&state.db)
    .await?;
    Ok(row.map(|r| r.0))
}

pub async fn enter(
    State(state): State<SharedState>,
    jar: CookieJar,
    Path(drama_id): Path<i64>,
    Form(form): Form<CsrfForm>,
) -> Result<Response> {
    let Ok((_session, player)) = action_context(&state, &jar, &form.csrf).await? else {
        return Ok(Redirect::to("/game/login").into_response());
    };
    let Some(drama) = fetch_drama(&state, drama_id).await? else {
        return Ok(redirect_msg("/game/play", "无该剧本，请重新选择~"));
    };
    if drama.status != 1 {
        return Ok(redirect_msg("/game/play", "该剧本暂未启用。"));
    }
    let script = match Script::parse(&drama.script_json) {
        Ok(s) if s.validate().is_ok() => s,
        _ => return Ok(redirect_msg("/game/play", "该剧本数据损坏，暂时无法进入。")),
    };

    let existing = sqlx::query_as::<_, ProgressRow>(
        "SELECT player_id, drama_id, node_id, status, updated_at FROM game_progress \
         WHERE player_id = ? AND drama_id = ?",
    )
    .bind(player.id)
    .bind(drama_id)
    .fetch_optional(&state.db)
    .await?;
    if let Some(p) = &existing {
        return Ok(redirect_msg(
            "/game/play",
            if p.status == STATUS_IN {
                "已参与该剧本，正在进行中。"
            } else {
                "你已完成或已中断该剧本，请先重置后再进入。"
            },
        ));
    }
    if let Some(other) = other_active_drama(&state, player.id, drama_id).await? {
        return Ok(redirect_msg(
            "/game/play",
            &format!("你正在剧本《{other}》中，请先退出或完成后再进入新剧本。"),
        ));
    }

    let now = now_ts();
    sqlx::query(
        "INSERT INTO game_progress (player_id, drama_id, node_id, status, updated_at) VALUES (?, ?, ?, ?, ?)",
    )
    .bind(player.id)
    .bind(drama_id)
    .bind(&script.start)
    .bind(STATUS_IN)
    .bind(now)
    .execute(&state.db)
    .await?;
    Ok(redirect_msg(
        "/game/play",
        &format!("进入剧本《{}》。", drama.name),
    ))
}

pub async fn exit(
    State(state): State<SharedState>,
    jar: CookieJar,
    Path(drama_id): Path<i64>,
    Form(form): Form<CsrfForm>,
) -> Result<Response> {
    let Ok((_session, player)) = action_context(&state, &jar, &form.csrf).await? else {
        return Ok(Redirect::to("/game/login").into_response());
    };
    let progress = sqlx::query_as::<_, ProgressRow>(
        "SELECT player_id, drama_id, node_id, status, updated_at FROM game_progress \
         WHERE player_id = ? AND drama_id = ?",
    )
    .bind(player.id)
    .bind(drama_id)
    .fetch_optional(&state.db)
    .await?;
    let Some(progress) = progress else {
        return Ok(redirect_msg("/game/play", "您还未参与该剧本。"));
    };
    if progress.status != STATUS_IN {
        return Ok(redirect_msg("/game/play", "该剧本不在进行中。"));
    }
    sqlx::query(
        "UPDATE game_progress SET status = ?, updated_at = ? WHERE player_id = ? AND drama_id = ?",
    )
    .bind(STATUS_INTERRUPTED)
    .bind(now_ts())
    .bind(player.id)
    .bind(drama_id)
    .execute(&state.db)
    .await?;
    Ok(redirect_msg("/game/play", "已中断该剧本。"))
}

pub async fn reset(
    State(state): State<SharedState>,
    jar: CookieJar,
    Path(drama_id): Path<i64>,
    Form(form): Form<CsrfForm>,
) -> Result<Response> {
    let Ok((_session, player)) = action_context(&state, &jar, &form.csrf).await? else {
        return Ok(Redirect::to("/game/login").into_response());
    };
    let Some(drama) = fetch_drama(&state, drama_id).await? else {
        return Ok(redirect_msg("/game/play", "无该剧本。"));
    };
    let Some(progress) = sqlx::query_as::<_, ProgressRow>(
        "SELECT player_id, drama_id, node_id, status, updated_at FROM game_progress \
         WHERE player_id = ? AND drama_id = ?",
    )
    .bind(player.id)
    .bind(drama_id)
    .fetch_optional(&state.db)
    .await?
    else {
        return Ok(redirect_msg("/game/play", "您还未参与该剧本。"));
    };
    let script = match Script::parse(&drama.script_json) {
        Ok(s) if s.validate().is_ok() => s,
        _ => return Ok(redirect_msg("/game/play", "该剧本数据损坏，无法重置。")),
    };
    if progress.status == STATUS_IN {
        return Ok(redirect_msg("/game/play", "该剧本正在进行中。"));
    }
    if let Some(other) = other_active_drama(&state, player.id, drama_id).await? {
        return Ok(redirect_msg(
            "/game/play",
            &format!("你正在剧本《{other}》中，请先退出后再重置该剧本。"),
        ));
    }
    sqlx::query(
        "UPDATE game_progress SET node_id = ?, status = ?, updated_at = ? \
         WHERE player_id = ? AND drama_id = ?",
    )
    .bind(&script.start)
    .bind(STATUS_IN)
    .bind(now_ts())
    .bind(player.id)
    .bind(drama_id)
    .execute(&state.db)
    .await?;
    Ok(redirect_msg("/game/play", "重置成功。"))
}

pub async fn choose(
    State(state): State<SharedState>,
    jar: CookieJar,
    Path((_drama_id, index)): Path<(i64, usize)>,
    Form(form): Form<CsrfForm>,
) -> Result<Response> {
    let Ok((_session, player)) = action_context(&state, &jar, &form.csrf).await? else {
        return Ok(Redirect::to("/game/login").into_response());
    };
    let Some(active) = sqlx::query_as::<_, ProgressRow>(
        "SELECT player_id, drama_id, node_id, status, updated_at FROM game_progress \
         WHERE player_id = ? AND status = ? ORDER BY updated_at DESC LIMIT 1",
    )
    .bind(player.id)
    .bind(STATUS_IN)
    .fetch_optional(&state.db)
    .await?
    else {
        return Ok(redirect_msg("/game/play", "您并未开始游戏。"));
    };
    let Some(drama) = fetch_drama(&state, active.drama_id).await? else {
        return Ok(redirect_msg("/game/play", "剧本不存在。"));
    };
    let script = match Script::parse(&drama.script_json) {
        Ok(s) => s,
        Err(e) => return Ok(redirect_msg("/game/play", &e)),
    };
    let Some(node) = script.node(&active.node_id) else {
        return Ok(redirect_msg("/game/play", "当前节点缺失，请联系管理员。"));
    };
    let next = match script.pick(node, index) {
        Ok(next) => next,
        Err(e) => return Ok(redirect_msg("/game/play", &e)),
    };
    let now = now_ts();
    if next.kind == "end" {
        sqlx::query(
            "UPDATE game_progress SET node_id = ?, status = ?, updated_at = ? \
             WHERE player_id = ? AND drama_id = ?",
        )
        .bind(&next.id)
        .bind(STATUS_FINISHED)
        .bind(now)
        .bind(player.id)
        .bind(active.drama_id)
        .execute(&state.db)
        .await?;
        Ok(redirect_msg(
            "/game/play",
            &format!("【完结】{}", next.description),
        ))
    } else {
        sqlx::query(
            "UPDATE game_progress SET node_id = ?, updated_at = ? WHERE player_id = ? AND drama_id = ?",
        )
        .bind(&next.id)
        .bind(now)
        .bind(player.id)
        .bind(active.drama_id)
        .execute(&state.db)
        .await?;
        Ok(redirect_msg(
            "/game/play",
            &format!("你选择了：{}", node.choices[index - 1].description),
        ))
    }
}

#[derive(Template)]
#[template(path = "game_login.html")]
struct LoginTemplate {
    ctx: crate::models::Ctx,
    error: String,
    username: String,
}

pub async fn login_page(State(state): State<SharedState>, jar: CookieJar) -> Result<Response> {
    if auth::player_session(&state, &jar).await.is_some() {
        return Ok(Redirect::to("/game/play").into_response());
    }
    let ctx = build_ctx(&state, &jar).await;
    Ok(HtmlTemplate(LoginTemplate {
        ctx,
        error: String::new(),
        username: String::new(),
    })
    .into_response())
}

#[derive(Deserialize)]
pub struct LoginForm {
    #[serde(default)]
    pub username: String,
    #[serde(default)]
    pub password: String,
}

pub async fn login(
    State(state): State<SharedState>,
    ConnectInfo(addr): ConnectInfo<SocketAddr>,
    jar: CookieJar,
    Form(form): Form<LoginForm>,
) -> Result<Response> {
    let username = form.username.trim().to_string();
    let key = auth::ip_key(&addr);
    let ctx = build_ctx(&state, &jar).await;
    if login_locked(&state, &key) {
        return Ok(HtmlTemplate(LoginTemplate {
            ctx,
            error: "尝试次数过多，请 1 分钟后再试。".to_string(),
            username,
        })
        .into_response());
    }
    let player = sqlx::query_as::<_, PlayerRow>(
        "SELECT id, username, password_hash, display_name, coins, points, level, \
                last_signin_at, created_at, updated_at FROM players WHERE username = ?",
    )
    .bind(&username)
    .fetch_optional(&state.db)
    .await?;

    let valid = player
        .as_ref()
        .map(|p| verify_password(&p.password_hash, &form.password))
        .unwrap_or(false);
    if !valid {
        login_failed(&state, &key);
        return Ok(HtmlTemplate(LoginTemplate {
            ctx,
            error: "用户名或密码错误。".to_string(),
            username,
        })
        .into_response());
    }
    login_succeeded(&state, &key);
    let player = player.expect("validated player");
    let (token, _csrf) =
        create_session(&state.db, KIND_PLAYER, player.id, PLAYER_SESSION_TTL).await?;
    let cookie = session_cookie(&state, COOKIE_PLAYER, &token, PLAYER_SESSION_TTL);
    Ok((jar.add(cookie), Redirect::to("/game/play")).into_response())
}

#[derive(Template)]
#[template(path = "game_register.html")]
struct RegisterTemplate {
    ctx: crate::models::Ctx,
    error: String,
    username: String,
    allow_registration: bool,
}

pub async fn register_page(State(state): State<SharedState>, jar: CookieJar) -> Result<Response> {
    if auth::player_session(&state, &jar).await.is_some() {
        return Ok(Redirect::to("/game/play").into_response());
    }
    let ctx = build_ctx(&state, &jar).await;
    Ok(HtmlTemplate(RegisterTemplate {
        ctx,
        error: String::new(),
        username: String::new(),
        allow_registration: state.cfg.allow_registration,
    })
    .into_response())
}

#[derive(Deserialize)]
pub struct RegisterForm {
    #[serde(default)]
    pub username: String,
    #[serde(default)]
    pub password: String,
    #[serde(default)]
    pub password2: String,
}

fn valid_username(username: &str) -> bool {
    let len = username.chars().count();
    (3..=32).contains(&len)
        && username
            .chars()
            .all(|c| c.is_ascii_alphanumeric() || c == '_')
}

pub async fn register(
    State(state): State<SharedState>,
    ConnectInfo(addr): ConnectInfo<SocketAddr>,
    jar: CookieJar,
    Form(form): Form<RegisterForm>,
) -> Result<Response> {
    let ctx = build_ctx(&state, &jar).await;
    let username = form.username.trim().to_string();
    if !state.cfg.allow_registration {
        return Ok((
            StatusCode::FORBIDDEN,
            HtmlTemplate(RegisterTemplate {
                ctx,
                error: "当前已关闭注册。".to_string(),
                username,
                allow_registration: false,
            }),
        )
            .into_response());
    }
    let key = auth::ip_key(&addr);
    if login_locked(&state, &key) {
        return Ok(HtmlTemplate(RegisterTemplate {
            ctx,
            error: "尝试次数过多，请 1 分钟后再试。".to_string(),
            username,
            allow_registration: true,
        })
        .into_response());
    }
    let fail = |ctx, username: String, error: &str| {
        HtmlTemplate(RegisterTemplate {
            ctx,
            error: error.to_string(),
            username,
            allow_registration: true,
        })
        .into_response()
    };
    if !valid_username(&username) {
        return Ok(fail(
            ctx,
            username,
            "用户名需为 3-32 位字母、数字或下划线。",
        ));
    }
    if form.password.chars().count() < 8 {
        return Ok(fail(ctx, username, "密码至少 8 位。"));
    }
    if form.password != form.password2 {
        return Ok(fail(ctx, username, "两次输入的密码不一致。"));
    }
    let exists: i64 = sqlx::query_scalar("SELECT COUNT(*) FROM players WHERE username = ?")
        .bind(&username)
        .fetch_one(&state.db)
        .await?;
    if exists > 0 {
        login_failed(&state, &key);
        return Ok(fail(ctx, username, "用户名已被占用。"));
    }
    let hash = auth::hash_password(&form.password)?;
    let now = now_ts();
    let result = sqlx::query(
        "INSERT INTO players (username, password_hash, display_name, coins, points, level, \
         last_signin_at, created_at, updated_at) VALUES (?, ?, ?, 0, 0, 0, ?, ?, ?)",
    )
    .bind(&username)
    .bind(&hash)
    .bind(&username)
    .bind(0i64)
    .bind(now)
    .bind(now)
    .execute(&state.db)
    .await;
    let player_id = match result {
        Ok(r) => r.last_insert_rowid(),
        Err(sqlx::Error::Database(_)) => {
            return Ok(fail(ctx, username, "用户名已被占用。"));
        }
        Err(e) => return Err(e.into()),
    };
    login_succeeded(&state, &key);
    let (token, _csrf) =
        create_session(&state.db, KIND_PLAYER, player_id, PLAYER_SESSION_TTL).await?;
    let cookie = session_cookie(&state, COOKIE_PLAYER, &token, PLAYER_SESSION_TTL);
    Ok((jar.add(cookie), Redirect::to("/game/play")).into_response())
}

pub async fn logout(
    State(state): State<SharedState>,
    jar: CookieJar,
    Form(form): Form<CsrfForm>,
) -> Result<Response> {
    let Some(session) = auth::player_session(&state, &jar).await else {
        return Ok(Redirect::to("/game").into_response());
    };
    check_csrf(&session, &form.csrf)?;
    if let Some(token) = auth::token_from_jar(&jar, COOKIE_PLAYER) {
        delete_session(&state.db, &token).await?;
    }
    Ok((
        jar.add(auth::clear_cookie(&state, COOKIE_PLAYER)),
        Redirect::to("/game"),
    )
        .into_response())
}
