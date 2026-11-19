pub mod assets;
pub mod manage;
pub mod player;
pub mod public;

use crate::auth::{self, Session};
use crate::error::{AppError, Result};
use crate::models::{load_settings, setting, Ctx};
use crate::state::AppState;
use crate::util::year_now;
use askama::Template;
use axum::response::{Html, IntoResponse, Redirect, Response};
use axum_extra::extract::CookieJar;
use std::collections::HashMap;

pub struct HtmlTemplate<T>(pub T);

impl<T: Template> IntoResponse for HtmlTemplate<T> {
    fn into_response(self) -> Response {
        match self.0.render() {
            Ok(html) => Html(html).into_response(),
            Err(e) => AppError::Template(e).into_response(),
        }
    }
}

pub async fn build_ctx(state: &AppState, jar: &CookieJar) -> Ctx {
    let settings = load_settings(&state.db).await.unwrap_or_default();
    let admin = auth::admin_session(state, jar).await;
    let player = auth::player_session(state, jar).await;

    let mut ctx = Ctx {
        base_url: state.cfg.base_url.clone(),
        site_title: setting(&settings, "site_title", "狂or野 - 你喜欢哪一个我"),
        is_admin: admin.is_some(),
        player_id: 0,
        player_name: String::new(),
        csrf: admin.as_ref().map(|s| s.csrf.clone()).unwrap_or_default(),
        year: year_now(),
    };

    if let Some(session) = player {
        ctx.player_id = session.subject_id;
        ctx.csrf = session.csrf.clone();
        if let Ok(Some((display_name, username))) = sqlx::query_as::<_, (String, String)>(
            "SELECT display_name, username FROM players WHERE id = ?",
        )
        .bind(session.subject_id)
        .fetch_optional(&state.db)
        .await
        {
            ctx.player_name = if display_name.trim().is_empty() {
                username
            } else {
                display_name
            };
        }
    }

    ctx
}

pub async fn build_admin_ctx(state: &AppState, jar: &CookieJar, admin: &Session) -> Ctx {
    let settings = load_settings(&state.db).await.unwrap_or_default();
    let player = auth::player_session(state, jar).await;
    let mut player_name = String::new();
    let mut player_id = 0;
    if let Some(session) = player {
        player_id = session.subject_id;
        if let Ok(Some((display_name, username))) = sqlx::query_as::<_, (String, String)>(
            "SELECT display_name, username FROM players WHERE id = ?",
        )
        .bind(session.subject_id)
        .fetch_optional(&state.db)
        .await
        {
            player_name = if display_name.trim().is_empty() {
                username
            } else {
                display_name
            };
        }
    }
    Ctx {
        base_url: state.cfg.base_url.clone(),
        site_title: setting(&settings, "site_title", "狂or野 - 你喜欢哪一个我"),
        is_admin: true,
        player_id,
        player_name,
        csrf: admin.csrf.clone(),
        year: year_now(),
    }
}

#[allow(clippy::result_large_err)]
pub async fn require_admin(
    state: &AppState,
    jar: &CookieJar,
) -> std::result::Result<Session, Response> {
    match auth::admin_session(state, jar).await {
        Some(session) => Ok(session),
        None => Err(Redirect::to("/manage/login").into_response()),
    }
}

pub fn check_csrf(session: &Session, token: &str) -> Result<()> {
    if auth::csrf_matches(&session.csrf, token) {
        Ok(())
    } else {
        Err(AppError::Forbidden)
    }
}

pub fn redirect_msg(path: &str, message: &str) -> Response {
    let sep = if path.contains('?') { '&' } else { '?' };
    Redirect::to(&format!(
        "{path}{sep}msg={}",
        crate::models::urlencode(message)
    ))
    .into_response()
}

pub fn qparam(query: &HashMap<String, String>, key: &str) -> String {
    query
        .get(key)
        .cloned()
        .unwrap_or_default()
        .trim()
        .to_string()
}

pub fn page_param(query: &HashMap<String, String>) -> i64 {
    query
        .get("page")
        .and_then(|v| v.parse::<i64>().ok())
        .unwrap_or(1)
        .clamp(1, 10_000)
}

pub fn qs_pair(q: &str, tag: &str) -> String {
    let mut parts = Vec::new();
    if !q.is_empty() {
        parts.push(format!("q={}", crate::models::urlencode(q)));
    }
    if !tag.is_empty() {
        parts.push(format!("tag={}", crate::models::urlencode(tag)));
    }
    parts.join("&")
}
