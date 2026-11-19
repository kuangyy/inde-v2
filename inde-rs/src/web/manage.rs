use crate::auth::{
    self, create_session, delete_session, login_failed, login_locked, login_succeeded,
    session_cookie, verify_password, COOKIE_ADMIN, KIND_ADMIN, KIND_PLAYER,
};
use crate::error::{AppError, Result};
use crate::game::pretty_script;
use crate::models::{
    load_settings, pagination, render_markdown, setting, BookRow, Ctx, DramaRow, Pagination,
    PlayerRow, PlayerView, PostRow, UploadRow, UploadView,
};
use crate::state::SharedState;
use crate::util::{fmt_date, fmt_datetime, normalize_tags, now_ts, truncate};
use crate::web::{build_admin_ctx, check_csrf, redirect_msg, require_admin, HtmlTemplate};
use askama::Template;
use axum::extract::{ConnectInfo, Form, Multipart, Path, Query, State};
use axum::http::StatusCode;
use axum::response::{IntoResponse, Redirect, Response};
use axum::Json;
use axum_extra::extract::cookie::CookieJar;
use serde::Deserialize;
use serde_json::json;
use std::collections::HashMap;
use std::net::SocketAddr;

const ADMIN_SESSION_TTL: i64 = 12 * 3600;
const ADMIN_SESSION_REMEMBER_TTL: i64 = 7 * 24 * 3600;
const MAX_UPLOAD_BYTES: usize = 10 * 1024 * 1024;

#[derive(Deserialize, Default)]
pub struct CsrfOnly {
    #[serde(default)]
    pub csrf: String,
}

async fn guard(
    state: &SharedState,
    jar: &CookieJar,
) -> Result<std::result::Result<(auth::Session, Ctx), Response>> {
    match require_admin(state, jar).await {
        Ok(session) => {
            let ctx = build_admin_ctx(state, jar, &session).await;
            Ok(Ok((session, ctx)))
        }
        Err(response) => Ok(Err(response)),
    }
}

/* ------------------------------------------------------------------ */
/* Login / logout                                                      */
/* ------------------------------------------------------------------ */

#[derive(Template)]
#[template(path = "manage/login.html")]
struct LoginTemplate {
    site_title: String,
    error: String,
}

pub async fn login_page(State(state): State<SharedState>, jar: CookieJar) -> Result<Response> {
    if auth::admin_session(&state, &jar).await.is_some() {
        return Ok(Redirect::to("/manage").into_response());
    }
    let settings = load_settings(&state.db).await?;
    Ok(HtmlTemplate(LoginTemplate {
        site_title: setting(&settings, "site_title", "狂or野 - 你喜欢哪一个我"),
        error: String::new(),
    })
    .into_response())
}

#[derive(Deserialize)]
pub struct AdminLoginForm {
    #[serde(default)]
    pub password: String,
    #[serde(default)]
    pub remember: Option<String>,
}

pub async fn login(
    State(state): State<SharedState>,
    ConnectInfo(addr): ConnectInfo<SocketAddr>,
    jar: CookieJar,
    Form(form): Form<AdminLoginForm>,
) -> Result<Response> {
    let settings = load_settings(&state.db).await?;
    let site_title = setting(&settings, "site_title", "狂or野 - 你喜欢哪一个我");
    let key = auth::ip_key(&addr);
    if login_locked(&state, &key) {
        return Ok((
            StatusCode::TOO_MANY_REQUESTS,
            HtmlTemplate(LoginTemplate {
                site_title,
                error: "尝试次数过多，请 1 分钟后再试。".to_string(),
            }),
        )
            .into_response());
    }
    if !verify_password(&state.admin_hash, &form.password) {
        login_failed(&state, &key);
        return Ok((
            StatusCode::UNAUTHORIZED,
            HtmlTemplate(LoginTemplate {
                site_title,
                error: "密码错误。".to_string(),
            }),
        )
            .into_response());
    }
    login_succeeded(&state, &key);
    let ttl = if form.remember.is_some() {
        ADMIN_SESSION_REMEMBER_TTL
    } else {
        ADMIN_SESSION_TTL
    };
    let (token, _csrf) = create_session(&state.db, KIND_ADMIN, 0, ttl).await?;
    let cookie = session_cookie(&state, COOKIE_ADMIN, &token, ttl);
    Ok((jar.add(cookie), Redirect::to("/manage")).into_response())
}

pub async fn logout(
    State(state): State<SharedState>,
    jar: CookieJar,
    Form(form): Form<CsrfOnly>,
) -> Result<Response> {
    let Some(session) = auth::admin_session(&state, &jar).await else {
        return Ok(Redirect::to("/manage/login").into_response());
    };
    check_csrf(&session, &form.csrf)?;
    if let Some(token) = auth::token_from_jar(&jar, COOKIE_ADMIN) {
        delete_session(&state.db, &token).await?;
    }
    Ok((
        jar.add(auth::clear_cookie(&state, COOKIE_ADMIN)),
        Redirect::to("/manage/login"),
    )
        .into_response())
}

/* ------------------------------------------------------------------ */
/* Dashboard                                                           */
/* ------------------------------------------------------------------ */

#[derive(Default)]
struct Counts {
    posts: i64,
    published: i64,
    books: i64,
    photos: i64,
    dramas: i64,
    players: i64,
    uploads: i64,
}

#[derive(Template)]
#[template(path = "manage/dashboard.html")]
struct DashboardTemplate {
    ctx: Ctx,
    counts: Counts,
}

pub async fn dashboard(State(state): State<SharedState>, jar: CookieJar) -> Result<Response> {
    let Ok((_admin, ctx)) = guard(&state, &jar).await? else {
        return Ok(Redirect::to("/manage/login").into_response());
    };
    let counts = Counts {
        posts: count(&state, "SELECT COUNT(*) FROM posts").await?,
        published: count(&state, "SELECT COUNT(*) FROM posts WHERE published_at > 0").await?,
        books: count(&state, "SELECT COUNT(*) FROM books").await?,
        photos: count(&state, "SELECT COUNT(*) FROM photos").await?,
        dramas: count(&state, "SELECT COUNT(*) FROM dramas").await?,
        players: count(&state, "SELECT COUNT(*) FROM players").await?,
        uploads: count(&state, "SELECT COUNT(*) FROM uploads").await?,
    };
    Ok(HtmlTemplate(DashboardTemplate { ctx, counts }).into_response())
}

async fn count(state: &SharedState, sql: &str) -> Result<i64> {
    Ok(sqlx::query_scalar(sql).fetch_one(&state.db).await?)
}

/* ------------------------------------------------------------------ */
/* Posts                                                               */
/* ------------------------------------------------------------------ */

struct AdminPostRow {
    id: i64,
    title: String,
    status: String,
    updated: String,
    views: i64,
}

#[derive(Template)]
#[template(path = "manage/posts.html")]
struct PostsTemplate {
    ctx: Ctx,
    posts: Vec<AdminPostRow>,
    notice: String,
}

pub async fn posts(
    State(state): State<SharedState>,
    jar: CookieJar,
    Query(query): Query<HashMap<String, String>>,
) -> Result<Response> {
    let Ok((_admin, ctx)) = guard(&state, &jar).await? else {
        return Ok(Redirect::to("/manage/login").into_response());
    };
    let rows = sqlx::query_as::<_, PostRow>(
        "SELECT id, title, summary, tags, cover, body_md, views, likes, created_at, updated_at, published_at \
         FROM posts ORDER BY updated_at DESC, id DESC",
    )
    .fetch_all(&state.db)
    .await?;
    let posts = rows
        .iter()
        .map(|p| AdminPostRow {
            id: p.id,
            title: p.title.clone(),
            status: if p.published_at > 0 {
                "已发布".to_string()
            } else {
                "草稿".to_string()
            },
            updated: fmt_datetime(p.updated_at),
            views: p.views,
        })
        .collect();
    Ok(HtmlTemplate(PostsTemplate {
        ctx,
        posts,
        notice: query.get("msg").cloned().unwrap_or_default(),
    })
    .into_response())
}

#[derive(Template)]
#[template(path = "manage/post_form.html")]
struct PostFormTemplate {
    ctx: Ctx,
    is_new: bool,
    id: i64,
    title: String,
    summary: String,
    tags: String,
    cover: String,
    body_md: String,
    published: bool,
    error: String,
}

pub async fn post_new_page(State(state): State<SharedState>, jar: CookieJar) -> Result<Response> {
    let Ok((_admin, ctx)) = guard(&state, &jar).await? else {
        return Ok(Redirect::to("/manage/login").into_response());
    };
    Ok(HtmlTemplate(PostFormTemplate {
        ctx,
        is_new: true,
        id: 0,
        title: String::new(),
        summary: String::new(),
        tags: String::new(),
        cover: String::new(),
        body_md: String::new(),
        published: true,
        error: String::new(),
    })
    .into_response())
}

pub async fn post_edit_page(
    State(state): State<SharedState>,
    jar: CookieJar,
    Path(id): Path<i64>,
) -> Result<Response> {
    let Ok((_admin, ctx)) = guard(&state, &jar).await? else {
        return Ok(Redirect::to("/manage/login").into_response());
    };
    let row = sqlx::query_as::<_, PostRow>(
        "SELECT id, title, summary, tags, cover, body_md, views, likes, created_at, updated_at, published_at \
         FROM posts WHERE id = ?",
    )
    .bind(id)
    .fetch_optional(&state.db)
    .await?
    .ok_or(AppError::NotFound)?;
    Ok(HtmlTemplate(PostFormTemplate {
        ctx,
        is_new: false,
        id: row.id,
        title: row.title,
        summary: row.summary,
        tags: row.tags,
        cover: row.cover,
        body_md: row.body_md,
        published: row.published_at > 0,
        error: String::new(),
    })
    .into_response())
}

#[derive(Deserialize)]
pub struct PostForm {
    #[serde(default)]
    pub csrf: String,
    #[serde(default)]
    pub title: String,
    #[serde(default)]
    pub summary: String,
    #[serde(default)]
    pub tags: String,
    #[serde(default)]
    pub cover: String,
    #[serde(default)]
    pub body_md: String,
    #[serde(default)]
    pub published: Option<String>,
}

fn post_form_view(
    ctx: Ctx,
    form: &PostForm,
    is_new: bool,
    id: i64,
    error: String,
) -> PostFormTemplate {
    PostFormTemplate {
        ctx,
        is_new,
        id,
        title: form.title.clone(),
        summary: form.summary.clone(),
        tags: form.tags.clone(),
        cover: form.cover.clone(),
        body_md: form.body_md.clone(),
        published: form.published.is_some(),
        error,
    }
}

pub async fn post_create(
    State(state): State<SharedState>,
    jar: CookieJar,
    Form(form): Form<PostForm>,
) -> Result<Response> {
    let Ok((admin, ctx)) = guard(&state, &jar).await? else {
        return Ok(Redirect::to("/manage/login").into_response());
    };
    check_csrf(&admin, &form.csrf)?;
    if form.title.trim().is_empty() {
        return Ok(HtmlTemplate(post_form_view(
            ctx,
            &form,
            true,
            0,
            "标题不能为空。".to_string(),
        ))
        .into_response());
    }
    let now = now_ts();
    let published_at = if form.published.is_some() { now } else { 0 };
    sqlx::query(
        "INSERT INTO posts (title, summary, tags, cover, body_md, views, likes, created_at, updated_at, published_at) \
         VALUES (?, ?, ?, ?, ?, 0, 0, ?, ?, ?)",
    )
    .bind(form.title.trim())
    .bind(form.summary.trim())
    .bind(normalize_tags(&form.tags))
    .bind(form.cover.trim())
    .bind(&form.body_md)
    .bind(now)
    .bind(now)
    .bind(published_at)
    .execute(&state.db)
    .await?;
    Ok(redirect_msg("/manage/posts", "文章已保存。"))
}

pub async fn post_update(
    State(state): State<SharedState>,
    jar: CookieJar,
    Path(id): Path<i64>,
    Form(form): Form<PostForm>,
) -> Result<Response> {
    let Ok((admin, ctx)) = guard(&state, &jar).await? else {
        return Ok(Redirect::to("/manage/login").into_response());
    };
    check_csrf(&admin, &form.csrf)?;
    let existing = sqlx::query_as::<_, PostRow>(
        "SELECT id, title, summary, tags, cover, body_md, views, likes, created_at, updated_at, published_at \
         FROM posts WHERE id = ?",
    )
    .bind(id)
    .fetch_optional(&state.db)
    .await?
    .ok_or(AppError::NotFound)?;
    if form.title.trim().is_empty() {
        return Ok(HtmlTemplate(post_form_view(
            ctx,
            &form,
            false,
            id,
            "标题不能为空。".to_string(),
        ))
        .into_response());
    }
    let now = now_ts();
    let published_at = if form.published.is_some() {
        if existing.published_at > 0 {
            existing.published_at
        } else {
            now
        }
    } else {
        0
    };
    sqlx::query(
        "UPDATE posts SET title = ?, summary = ?, tags = ?, cover = ?, body_md = ?, updated_at = ?, published_at = ? WHERE id = ?",
    )
    .bind(form.title.trim())
    .bind(form.summary.trim())
    .bind(normalize_tags(&form.tags))
    .bind(form.cover.trim())
    .bind(&form.body_md)
    .bind(now)
    .bind(published_at)
    .bind(id)
    .execute(&state.db)
    .await?;
    Ok(redirect_msg("/manage/posts", "文章已更新。"))
}

pub async fn post_delete(
    State(state): State<SharedState>,
    jar: CookieJar,
    Path(id): Path<i64>,
    Form(form): Form<CsrfOnly>,
) -> Result<Response> {
    let Ok((admin, _ctx)) = guard(&state, &jar).await? else {
        return Ok(Redirect::to("/manage/login").into_response());
    };
    check_csrf(&admin, &form.csrf)?;
    sqlx::query("DELETE FROM posts WHERE id = ?")
        .bind(id)
        .execute(&state.db)
        .await?;
    Ok(redirect_msg("/manage/posts", "文章已删除。"))
}

/* ------------------------------------------------------------------ */
/* Books                                                               */
/* ------------------------------------------------------------------ */

struct AdminBookRow {
    id: i64,
    name: String,
    author: String,
    score: String,
    updated: String,
}

#[derive(Template)]
#[template(path = "manage/books.html")]
struct BooksTemplate {
    ctx: Ctx,
    books: Vec<AdminBookRow>,
    notice: String,
}

pub async fn books(
    State(state): State<SharedState>,
    jar: CookieJar,
    Query(query): Query<HashMap<String, String>>,
) -> Result<Response> {
    let Ok((_admin, ctx)) = guard(&state, &jar).await? else {
        return Ok(Redirect::to("/manage/login").into_response());
    };
    let rows = sqlx::query_as::<_, BookRow>(
        "SELECT id, name, author, score, summary, cover, download_url, content_md, views, created_at, updated_at \
         FROM books ORDER BY updated_at DESC, id DESC",
    )
    .fetch_all(&state.db)
    .await?;
    let books = rows
        .iter()
        .map(|b| AdminBookRow {
            id: b.id,
            name: b.name.clone(),
            author: b.author.clone(),
            score: format!("{:.1}", b.score),
            updated: fmt_datetime(b.updated_at),
        })
        .collect();
    Ok(HtmlTemplate(BooksTemplate {
        ctx,
        books,
        notice: query.get("msg").cloned().unwrap_or_default(),
    })
    .into_response())
}

#[derive(Template)]
#[template(path = "manage/book_form.html")]
struct BookFormTemplate {
    ctx: Ctx,
    is_new: bool,
    id: i64,
    name: String,
    author: String,
    score: String,
    summary: String,
    cover: String,
    download_url: String,
    content_md: String,
    error: String,
}

pub async fn book_new_page(State(state): State<SharedState>, jar: CookieJar) -> Result<Response> {
    let Ok((_admin, ctx)) = guard(&state, &jar).await? else {
        return Ok(Redirect::to("/manage/login").into_response());
    };
    Ok(HtmlTemplate(BookFormTemplate {
        ctx,
        is_new: true,
        id: 0,
        name: String::new(),
        author: String::new(),
        score: "0".to_string(),
        summary: String::new(),
        cover: String::new(),
        download_url: String::new(),
        content_md: String::new(),
        error: String::new(),
    })
    .into_response())
}

pub async fn book_edit_page(
    State(state): State<SharedState>,
    jar: CookieJar,
    Path(id): Path<i64>,
) -> Result<Response> {
    let Ok((_admin, ctx)) = guard(&state, &jar).await? else {
        return Ok(Redirect::to("/manage/login").into_response());
    };
    let row = sqlx::query_as::<_, BookRow>(
        "SELECT id, name, author, score, summary, cover, download_url, content_md, views, created_at, updated_at \
         FROM books WHERE id = ?",
    )
    .bind(id)
    .fetch_optional(&state.db)
    .await?
    .ok_or(AppError::NotFound)?;
    Ok(HtmlTemplate(BookFormTemplate {
        ctx,
        is_new: false,
        id: row.id,
        name: row.name,
        author: row.author,
        score: format!("{:.1}", row.score),
        summary: row.summary,
        cover: row.cover,
        download_url: row.download_url,
        content_md: row.content_md,
        error: String::new(),
    })
    .into_response())
}

#[derive(Deserialize)]
pub struct BookForm {
    #[serde(default)]
    pub csrf: String,
    #[serde(default)]
    pub name: String,
    #[serde(default)]
    pub author: String,
    #[serde(default)]
    pub score: String,
    #[serde(default)]
    pub summary: String,
    #[serde(default)]
    pub cover: String,
    #[serde(default)]
    pub download_url: String,
    #[serde(default)]
    pub content_md: String,
}

fn book_form_view(
    ctx: Ctx,
    form: &BookForm,
    is_new: bool,
    id: i64,
    error: String,
) -> BookFormTemplate {
    BookFormTemplate {
        ctx,
        is_new,
        id,
        name: form.name.clone(),
        author: form.author.clone(),
        score: form.score.clone(),
        summary: form.summary.clone(),
        cover: form.cover.clone(),
        download_url: form.download_url.clone(),
        content_md: form.content_md.clone(),
        error,
    }
}

pub async fn book_create(
    State(state): State<SharedState>,
    jar: CookieJar,
    Form(form): Form<BookForm>,
) -> Result<Response> {
    let Ok((admin, ctx)) = guard(&state, &jar).await? else {
        return Ok(Redirect::to("/manage/login").into_response());
    };
    check_csrf(&admin, &form.csrf)?;
    if form.name.trim().is_empty() {
        return Ok(HtmlTemplate(book_form_view(
            ctx,
            &form,
            true,
            0,
            "书名不能为空。".to_string(),
        ))
        .into_response());
    }
    let score = parse_score(&form.score);
    let now = now_ts();
    sqlx::query(
        "INSERT INTO books (name, author, score, summary, cover, download_url, content_md, views, created_at, updated_at) \
         VALUES (?, ?, ?, ?, ?, ?, ?, 0, ?, ?)",
    )
    .bind(form.name.trim())
    .bind(form.author.trim())
    .bind(score)
    .bind(form.summary.trim())
    .bind(form.cover.trim())
    .bind(form.download_url.trim())
    .bind(&form.content_md)
    .bind(now)
    .bind(now)
    .execute(&state.db)
    .await?;
    Ok(redirect_msg("/manage/books", "书籍已保存。"))
}

pub async fn book_update(
    State(state): State<SharedState>,
    jar: CookieJar,
    Path(id): Path<i64>,
    Form(form): Form<BookForm>,
) -> Result<Response> {
    let Ok((admin, ctx)) = guard(&state, &jar).await? else {
        return Ok(Redirect::to("/manage/login").into_response());
    };
    check_csrf(&admin, &form.csrf)?;
    let exists: i64 = sqlx::query_scalar("SELECT COUNT(*) FROM books WHERE id = ?")
        .bind(id)
        .fetch_one(&state.db)
        .await?;
    if exists == 0 {
        return Err(AppError::NotFound);
    }
    if form.name.trim().is_empty() {
        return Ok(HtmlTemplate(book_form_view(
            ctx,
            &form,
            false,
            id,
            "书名不能为空。".to_string(),
        ))
        .into_response());
    }
    sqlx::query(
        "UPDATE books SET name = ?, author = ?, score = ?, summary = ?, cover = ?, download_url = ?, content_md = ?, updated_at = ? WHERE id = ?",
    )
    .bind(form.name.trim())
    .bind(form.author.trim())
    .bind(parse_score(&form.score))
    .bind(form.summary.trim())
    .bind(form.cover.trim())
    .bind(form.download_url.trim())
    .bind(&form.content_md)
    .bind(now_ts())
    .bind(id)
    .execute(&state.db)
    .await?;
    Ok(redirect_msg("/manage/books", "书籍已更新。"))
}

fn parse_score(raw: &str) -> f64 {
    raw.trim().parse::<f64>().unwrap_or(0.0).clamp(0.0, 10.0)
}

pub async fn book_delete(
    State(state): State<SharedState>,
    jar: CookieJar,
    Path(id): Path<i64>,
    Form(form): Form<CsrfOnly>,
) -> Result<Response> {
    let Ok((admin, _ctx)) = guard(&state, &jar).await? else {
        return Ok(Redirect::to("/manage/login").into_response());
    };
    check_csrf(&admin, &form.csrf)?;
    sqlx::query("DELETE FROM books WHERE id = ?")
        .bind(id)
        .execute(&state.db)
        .await?;
    Ok(redirect_msg("/manage/books", "书籍已删除。"))
}

/* ------------------------------------------------------------------ */
/* Photos                                                              */
/* ------------------------------------------------------------------ */

#[derive(Template)]
#[template(path = "manage/photos.html")]
struct PhotosTemplate {
    ctx: Ctx,
    photos: Vec<UploadView>,
    notice: String,
}

pub async fn photos(
    State(state): State<SharedState>,
    jar: CookieJar,
    Query(query): Query<HashMap<String, String>>,
) -> Result<Response> {
    let Ok((_admin, ctx)) = guard(&state, &jar).await? else {
        return Ok(Redirect::to("/manage/login").into_response());
    };
    let rows = sqlx::query_as::<_, crate::models::PhotoRow>(
        "SELECT id, name, url, sort_order, created_at FROM photos ORDER BY sort_order ASC, id DESC",
    )
    .fetch_all(&state.db)
    .await?;
    let photos = rows
        .iter()
        .map(|p| UploadView {
            id: p.id,
            url: p.url.clone(),
            path: p.url.clone(),
            original_name: p.name.clone(),
            size: format!("排序 {}", p.sort_order),
            created: fmt_date(p.created_at),
        })
        .collect();
    Ok(HtmlTemplate(PhotosTemplate {
        ctx,
        photos,
        notice: query.get("msg").cloned().unwrap_or_default(),
    })
    .into_response())
}

#[derive(Template)]
#[template(path = "manage/photo_form.html")]
struct PhotoFormTemplate {
    ctx: Ctx,
    is_new: bool,
    id: i64,
    name: String,
    url: String,
    sort_order: String,
    error: String,
}

pub async fn photo_new_page(State(state): State<SharedState>, jar: CookieJar) -> Result<Response> {
    let Ok((_admin, ctx)) = guard(&state, &jar).await? else {
        return Ok(Redirect::to("/manage/login").into_response());
    };
    Ok(HtmlTemplate(PhotoFormTemplate {
        ctx,
        is_new: true,
        id: 0,
        name: String::new(),
        url: String::new(),
        sort_order: "0".to_string(),
        error: String::new(),
    })
    .into_response())
}

pub async fn photo_edit_page(
    State(state): State<SharedState>,
    jar: CookieJar,
    Path(id): Path<i64>,
) -> Result<Response> {
    let Ok((_admin, ctx)) = guard(&state, &jar).await? else {
        return Ok(Redirect::to("/manage/login").into_response());
    };
    let row = sqlx::query_as::<_, crate::models::PhotoRow>(
        "SELECT id, name, url, sort_order, created_at FROM photos WHERE id = ?",
    )
    .bind(id)
    .fetch_optional(&state.db)
    .await?
    .ok_or(AppError::NotFound)?;
    Ok(HtmlTemplate(PhotoFormTemplate {
        ctx,
        is_new: false,
        id: row.id,
        name: row.name,
        url: row.url,
        sort_order: row.sort_order.to_string(),
        error: String::new(),
    })
    .into_response())
}

#[derive(Deserialize)]
pub struct PhotoForm {
    #[serde(default)]
    pub csrf: String,
    #[serde(default)]
    pub name: String,
    #[serde(default)]
    pub url: String,
    #[serde(default)]
    pub sort_order: String,
}

pub async fn photo_create(
    State(state): State<SharedState>,
    jar: CookieJar,
    Form(form): Form<PhotoForm>,
) -> Result<Response> {
    let Ok((admin, ctx)) = guard(&state, &jar).await? else {
        return Ok(Redirect::to("/manage/login").into_response());
    };
    check_csrf(&admin, &form.csrf)?;
    let url = form.url.trim();
    if !(url.starts_with('/') || url.starts_with("https://") || url.starts_with("http://")) {
        return Ok(HtmlTemplate(PhotoFormTemplate {
            ctx,
            is_new: true,
            id: 0,
            name: form.name.clone(),
            url: form.url.clone(),
            sort_order: form.sort_order.clone(),
            error: "图片地址需为 /uploads/... 或完整 http(s) 链接。".to_string(),
        })
        .into_response());
    }
    sqlx::query("INSERT INTO photos (name, url, sort_order, created_at) VALUES (?, ?, ?, ?)")
        .bind(form.name.trim())
        .bind(url)
        .bind(parse_sort(&form.sort_order))
        .bind(now_ts())
        .execute(&state.db)
        .await?;
    Ok(redirect_msg("/manage/photos", "图片已添加。"))
}

pub async fn photo_update(
    State(state): State<SharedState>,
    jar: CookieJar,
    Path(id): Path<i64>,
    Form(form): Form<PhotoForm>,
) -> Result<Response> {
    let Ok((admin, ctx)) = guard(&state, &jar).await? else {
        return Ok(Redirect::to("/manage/login").into_response());
    };
    check_csrf(&admin, &form.csrf)?;
    let url = form.url.trim();
    if !(url.starts_with('/') || url.starts_with("https://") || url.starts_with("http://")) {
        return Ok(HtmlTemplate(PhotoFormTemplate {
            ctx,
            is_new: false,
            id,
            name: form.name.clone(),
            url: form.url.clone(),
            sort_order: form.sort_order.clone(),
            error: "图片地址需为 /uploads/... 或完整 http(s) 链接。".to_string(),
        })
        .into_response());
    }
    sqlx::query("UPDATE photos SET name = ?, url = ?, sort_order = ? WHERE id = ?")
        .bind(form.name.trim())
        .bind(url)
        .bind(parse_sort(&form.sort_order))
        .bind(id)
        .execute(&state.db)
        .await?;
    Ok(redirect_msg("/manage/photos", "图片已更新。"))
}

fn parse_sort(raw: &str) -> i64 {
    raw.trim().parse::<i64>().unwrap_or(0)
}

pub async fn photo_delete(
    State(state): State<SharedState>,
    jar: CookieJar,
    Path(id): Path<i64>,
    Form(form): Form<CsrfOnly>,
) -> Result<Response> {
    let Ok((admin, _ctx)) = guard(&state, &jar).await? else {
        return Ok(Redirect::to("/manage/login").into_response());
    };
    check_csrf(&admin, &form.csrf)?;
    sqlx::query("DELETE FROM photos WHERE id = ?")
        .bind(id)
        .execute(&state.db)
        .await?;
    Ok(redirect_msg("/manage/photos", "图片已删除。"))
}

/* ------------------------------------------------------------------ */
/* Dramas                                                              */
/* ------------------------------------------------------------------ */

struct AdminDramaRow {
    id: i64,
    name: String,
    description: String,
    status: String,
    updated: String,
}

#[derive(Template)]
#[template(path = "manage/dramas.html")]
struct DramasTemplate {
    ctx: Ctx,
    dramas: Vec<AdminDramaRow>,
    notice: String,
}

pub async fn dramas(
    State(state): State<SharedState>,
    jar: CookieJar,
    Query(query): Query<HashMap<String, String>>,
) -> Result<Response> {
    let Ok((_admin, ctx)) = guard(&state, &jar).await? else {
        return Ok(Redirect::to("/manage/login").into_response());
    };
    let rows = sqlx::query_as::<_, DramaRow>(
        "SELECT id, name, description, status, script_json, created_at, updated_at FROM dramas ORDER BY id ASC",
    )
    .fetch_all(&state.db)
    .await?;
    let dramas = rows
        .iter()
        .map(|d| AdminDramaRow {
            id: d.id,
            name: d.name.clone(),
            description: truncate(&d.description, 60),
            status: if d.status == 1 {
                "已启用".to_string()
            } else {
                "已停用".to_string()
            },
            updated: fmt_datetime(d.updated_at),
        })
        .collect();
    Ok(HtmlTemplate(DramasTemplate {
        ctx,
        dramas,
        notice: query.get("msg").cloned().unwrap_or_default(),
    })
    .into_response())
}

#[derive(Template)]
#[template(path = "manage/drama_form.html")]
struct DramaFormTemplate {
    ctx: Ctx,
    is_new: bool,
    id: i64,
    name: String,
    description: String,
    enabled: bool,
    script_json: String,
    error: String,
}

pub async fn drama_new_page(State(state): State<SharedState>, jar: CookieJar) -> Result<Response> {
    let Ok((_admin, ctx)) = guard(&state, &jar).await? else {
        return Ok(Redirect::to("/manage/login").into_response());
    };
    Ok(HtmlTemplate(DramaFormTemplate {
        ctx,
        is_new: true,
        id: 0,
        name: String::new(),
        description: String::new(),
        enabled: false,
        script_json: "{\n  \"start\": \"node-1\",\n  \"nodes\": [\n    {\"id\": \"node-1\", \"kind\": \"scene\", \"description\": \"场景描述\", \"choices\": [{\"description\": \"选项一\", \"next\": \"node-2\"}]},\n    {\"id\": \"node-2\", \"kind\": \"end\", \"description\": \"结局描述\", \"choices\": []}\n  ]\n}"
            .to_string(),
        error: String::new(),
    })
    .into_response())
}

pub async fn drama_edit_page(
    State(state): State<SharedState>,
    jar: CookieJar,
    Path(id): Path<i64>,
) -> Result<Response> {
    let Ok((_admin, ctx)) = guard(&state, &jar).await? else {
        return Ok(Redirect::to("/manage/login").into_response());
    };
    let row = sqlx::query_as::<_, DramaRow>(
        "SELECT id, name, description, status, script_json, created_at, updated_at FROM dramas WHERE id = ?",
    )
    .bind(id)
    .fetch_optional(&state.db)
    .await?
    .ok_or(AppError::NotFound)?;
    Ok(HtmlTemplate(DramaFormTemplate {
        ctx,
        is_new: false,
        id: row.id,
        name: row.name,
        description: row.description,
        enabled: row.status == 1,
        script_json: row.script_json,
        error: String::new(),
    })
    .into_response())
}

#[derive(Deserialize)]
pub struct DramaForm {
    #[serde(default)]
    pub csrf: String,
    #[serde(default)]
    pub name: String,
    #[serde(default)]
    pub description: String,
    #[serde(default)]
    pub enabled: Option<String>,
    #[serde(default)]
    pub script_json: String,
}

fn drama_form_view(
    ctx: Ctx,
    form: &DramaForm,
    is_new: bool,
    id: i64,
    error: String,
) -> DramaFormTemplate {
    DramaFormTemplate {
        ctx,
        is_new,
        id,
        name: form.name.clone(),
        description: form.description.clone(),
        enabled: form.enabled.is_some(),
        script_json: form.script_json.clone(),
        error,
    }
}

pub async fn drama_create(
    State(state): State<SharedState>,
    jar: CookieJar,
    Form(form): Form<DramaForm>,
) -> Result<Response> {
    let Ok((admin, ctx)) = guard(&state, &jar).await? else {
        return Ok(Redirect::to("/manage/login").into_response());
    };
    check_csrf(&admin, &form.csrf)?;
    let error = validate_drama_form(&form);
    if let Some(error) = error {
        return Ok(HtmlTemplate(drama_form_view(ctx, &form, true, 0, error)).into_response());
    }
    let pretty = pretty_script(&form.script_json).unwrap_or_else(|_| form.script_json.clone());
    let now = now_ts();
    sqlx::query(
        "INSERT INTO dramas (name, description, status, script_json, created_at, updated_at) VALUES (?, ?, ?, ?, ?, ?)",
    )
    .bind(form.name.trim())
    .bind(form.description.trim())
    .bind(if form.enabled.is_some() { 1 } else { 0 })
    .bind(pretty)
    .bind(now)
    .bind(now)
    .execute(&state.db)
    .await?;
    Ok(redirect_msg("/manage/dramas", "剧本已保存。"))
}

pub async fn drama_update(
    State(state): State<SharedState>,
    jar: CookieJar,
    Path(id): Path<i64>,
    Form(form): Form<DramaForm>,
) -> Result<Response> {
    let Ok((admin, ctx)) = guard(&state, &jar).await? else {
        return Ok(Redirect::to("/manage/login").into_response());
    };
    check_csrf(&admin, &form.csrf)?;
    let exists: i64 = sqlx::query_scalar("SELECT COUNT(*) FROM dramas WHERE id = ?")
        .bind(id)
        .fetch_one(&state.db)
        .await?;
    if exists == 0 {
        return Err(AppError::NotFound);
    }
    let error = validate_drama_form(&form);
    if let Some(error) = error {
        return Ok(HtmlTemplate(drama_form_view(ctx, &form, false, id, error)).into_response());
    }
    let pretty = pretty_script(&form.script_json).unwrap_or_else(|_| form.script_json.clone());
    sqlx::query(
        "UPDATE dramas SET name = ?, description = ?, status = ?, script_json = ?, updated_at = ? WHERE id = ?",
    )
    .bind(form.name.trim())
    .bind(form.description.trim())
    .bind(if form.enabled.is_some() { 1 } else { 0 })
    .bind(pretty)
    .bind(now_ts())
    .bind(id)
    .execute(&state.db)
    .await?;
    Ok(redirect_msg("/manage/dramas", "剧本已更新。"))
}

fn validate_drama_form(form: &DramaForm) -> Option<String> {
    if form.name.trim().is_empty() {
        return Some("剧本名称不能为空。".to_string());
    }
    pretty_script(&form.script_json).err()
}

pub async fn drama_delete(
    State(state): State<SharedState>,
    jar: CookieJar,
    Path(id): Path<i64>,
    Form(form): Form<CsrfOnly>,
) -> Result<Response> {
    let Ok((admin, _ctx)) = guard(&state, &jar).await? else {
        return Ok(Redirect::to("/manage/login").into_response());
    };
    check_csrf(&admin, &form.csrf)?;
    sqlx::query("DELETE FROM dramas WHERE id = ?")
        .bind(id)
        .execute(&state.db)
        .await?;
    Ok(redirect_msg("/manage/dramas", "剧本已删除。"))
}

/* ------------------------------------------------------------------ */
/* Settings and mottos                                                 */
/* ------------------------------------------------------------------ */

struct MottoRowView {
    id: i64,
    name: String,
    translate: String,
}

#[derive(Template)]
#[template(path = "manage/settings.html")]
struct SettingsTemplate {
    ctx: Ctx,
    site_title: String,
    site_subtitle: String,
    audio_url: String,
    about_html: String,
    mottos: Vec<MottoRowView>,
    notice: String,
}

pub async fn settings_page(
    State(state): State<SharedState>,
    jar: CookieJar,
    Query(query): Query<HashMap<String, String>>,
) -> Result<Response> {
    let Ok((_admin, ctx)) = guard(&state, &jar).await? else {
        return Ok(Redirect::to("/manage/login").into_response());
    };
    let settings = load_settings(&state.db).await?;
    let mottos = sqlx::query_as::<_, (i64, String, String)>(
        "SELECT id, name, translate FROM mottos ORDER BY id DESC",
    )
    .fetch_all(&state.db)
    .await?
    .into_iter()
    .map(|(id, name, translate)| MottoRowView {
        id,
        name,
        translate,
    })
    .collect();
    Ok(HtmlTemplate(SettingsTemplate {
        ctx,
        site_title: setting(&settings, "site_title", ""),
        site_subtitle: setting(&settings, "site_subtitle", ""),
        audio_url: setting(&settings, "audio_url", "/assets/audio/a.mp3"),
        about_html: setting(&settings, "about_html", ""),
        mottos,
        notice: query.get("msg").cloned().unwrap_or_default(),
    })
    .into_response())
}

#[derive(Deserialize)]
pub struct SettingsForm {
    #[serde(default)]
    pub csrf: String,
    #[serde(default)]
    pub site_title: String,
    #[serde(default)]
    pub site_subtitle: String,
    #[serde(default)]
    pub audio_url: String,
    #[serde(default)]
    pub about_html: String,
}

pub async fn settings_save(
    State(state): State<SharedState>,
    jar: CookieJar,
    Form(form): Form<SettingsForm>,
) -> Result<Response> {
    let Ok((admin, _ctx)) = guard(&state, &jar).await? else {
        return Ok(Redirect::to("/manage/login").into_response());
    };
    check_csrf(&admin, &form.csrf)?;
    let pairs = [
        ("site_title", form.site_title.trim()),
        ("site_subtitle", form.site_subtitle.trim()),
        ("audio_url", form.audio_url.trim()),
        ("about_html", form.about_html.trim()),
    ];
    for (key, value) in pairs {
        sqlx::query(
            "INSERT INTO settings (key, value) VALUES (?, ?) ON CONFLICT(key) DO UPDATE SET value = excluded.value",
        )
        .bind(key)
        .bind(value)
        .execute(&state.db)
        .await?;
    }
    Ok(redirect_msg("/manage/settings", "设置已保存。"))
}

#[derive(Deserialize)]
pub struct MottoForm {
    #[serde(default)]
    pub csrf: String,
    #[serde(default)]
    pub name: String,
    #[serde(default)]
    pub translate: String,
}

pub async fn motto_add(
    State(state): State<SharedState>,
    jar: CookieJar,
    Form(form): Form<MottoForm>,
) -> Result<Response> {
    let Ok((admin, _ctx)) = guard(&state, &jar).await? else {
        return Ok(Redirect::to("/manage/login").into_response());
    };
    check_csrf(&admin, &form.csrf)?;
    if form.name.trim().is_empty() {
        return Ok(redirect_msg("/manage/settings", "格言内容不能为空。"));
    }
    sqlx::query("INSERT INTO mottos (name, translate, created_at) VALUES (?, ?, ?)")
        .bind(form.name.trim())
        .bind(form.translate.trim())
        .bind(now_ts())
        .execute(&state.db)
        .await?;
    Ok(redirect_msg("/manage/settings", "格言已添加。"))
}

pub async fn motto_delete(
    State(state): State<SharedState>,
    jar: CookieJar,
    Path(id): Path<i64>,
    Form(form): Form<CsrfOnly>,
) -> Result<Response> {
    let Ok((admin, _ctx)) = guard(&state, &jar).await? else {
        return Ok(Redirect::to("/manage/login").into_response());
    };
    check_csrf(&admin, &form.csrf)?;
    sqlx::query("DELETE FROM mottos WHERE id = ?")
        .bind(id)
        .execute(&state.db)
        .await?;
    Ok(redirect_msg("/manage/settings", "格言已删除。"))
}

/* ------------------------------------------------------------------ */
/* Uploads                                                             */
/* ------------------------------------------------------------------ */

#[derive(Template)]
#[template(path = "manage/uploads.html")]
struct UploadsTemplate {
    ctx: Ctx,
    uploads: Vec<UploadView>,
    notice: String,
    pag: Pagination,
}

pub async fn uploads_page(
    State(state): State<SharedState>,
    jar: CookieJar,
    Query(query): Query<HashMap<String, String>>,
) -> Result<Response> {
    let Ok((_admin, ctx)) = guard(&state, &jar).await? else {
        return Ok(Redirect::to("/manage/login").into_response());
    };
    let per_page = 40i64;
    let total: i64 = sqlx::query_scalar("SELECT COUNT(*) FROM uploads")
        .fetch_one(&state.db)
        .await?;
    let page = query
        .get("page")
        .and_then(|v| v.parse::<i64>().ok())
        .unwrap_or(1)
        .clamp(1, 10_000);
    let pag = pagination(page, total, per_page);
    let rows = sqlx::query_as::<_, UploadRow>(
        "SELECT id, path, original_name, mime, size, created_at FROM uploads \
         ORDER BY created_at DESC, id DESC LIMIT ? OFFSET ?",
    )
    .bind(per_page)
    .bind((pag.page - 1) * per_page)
    .fetch_all(&state.db)
    .await?;
    let uploads = rows.iter().map(UploadView::from).collect();
    Ok(HtmlTemplate(UploadsTemplate {
        ctx,
        uploads,
        notice: query.get("msg").cloned().unwrap_or_default(),
        pag,
    })
    .into_response())
}

struct IncomingUpload {
    csrf: String,
    filename: String,
    data: Vec<u8>,
}

async fn parse_upload(mut multipart: Multipart) -> std::result::Result<IncomingUpload, String> {
    let mut csrf = String::new();
    let mut filename = String::new();
    let mut data: Vec<u8> = Vec::new();
    while let Some(field) = multipart
        .next_field()
        .await
        .map_err(|e| format!("上传解析失败：{e}"))?
    {
        let name = field.name().unwrap_or("").to_string();
        match name.as_str() {
            "csrf" => {
                csrf = field
                    .text()
                    .await
                    .map_err(|e| format!("上传解析失败：{e}"))?;
            }
            "file" => {
                filename = field.file_name().unwrap_or("upload").to_string();
                let bytes = field
                    .bytes()
                    .await
                    .map_err(|e| format!("读取上传文件失败：{e}"))?;
                data = bytes.to_vec();
            }
            _ => {}
        }
    }
    Ok(IncomingUpload {
        csrf,
        filename,
        data,
    })
}

fn detect_image_ext(data: &[u8]) -> Option<&'static str> {
    if data.starts_with(&[0x89, b'P', b'N', b'G', 0x0d, 0x0a, 0x1a, 0x0a]) {
        return Some("png");
    }
    if data.starts_with(&[0xff, 0xd8, 0xff]) {
        return Some("jpg");
    }
    if data.starts_with(b"GIF87a") || data.starts_with(b"GIF89a") {
        return Some("gif");
    }
    if data.len() > 12 && &data[0..4] == b"RIFF" && &data[8..12] == b"WEBP" {
        return Some("webp");
    }
    None
}

async fn store_upload(
    state: &SharedState,
    admin: &auth::Session,
    incoming: IncomingUpload,
) -> std::result::Result<String, String> {
    if !crate::auth::csrf_matches(&admin.csrf, &incoming.csrf) {
        return Err("CSRF 校验失败，请刷新页面后重试。".to_string());
    }
    if incoming.data.is_empty() {
        return Err("请选择要上传的图片。".to_string());
    }
    if incoming.data.len() > MAX_UPLOAD_BYTES {
        return Err("图片不能超过 10MB。".to_string());
    }
    let Some(ext) = detect_image_ext(&incoming.data) else {
        return Err("仅支持 PNG / JPG / WEBP / GIF 图片。".to_string());
    };
    let date = fmt_date(now_ts());
    let mut parts = date.split('-');
    let year = parts.next().unwrap_or("1970");
    let month = parts.next().unwrap_or("01");
    let subdir = format!("{year}/{month}");
    let file_name = format!("{}.{}", uuid::Uuid::new_v4(), ext);
    let dir = state.cfg.uploads_dir().join(year).join(month);
    tokio::fs::create_dir_all(&dir)
        .await
        .map_err(|e| format!("无法创建上传目录：{e}"))?;
    tokio::fs::write(dir.join(&file_name), &incoming.data)
        .await
        .map_err(|e| format!("写入文件失败：{e}"))?;
    let rel_path = format!("{subdir}/{file_name}");
    let mime = match ext {
        "png" => "image/png",
        "jpg" => "image/jpeg",
        "gif" => "image/gif",
        "webp" => "image/webp",
        _ => "application/octet-stream",
    };
    sqlx::query(
        "INSERT INTO uploads (path, original_name, mime, size, created_at) VALUES (?, ?, ?, ?, ?)",
    )
    .bind(&rel_path)
    .bind(truncate(&incoming.filename, 200))
    .bind(mime)
    .bind(incoming.data.len() as i64)
    .bind(now_ts())
    .execute(&state.db)
    .await
    .map_err(|e| format!("写入数据库失败：{e}"))?;
    Ok(format!("/uploads/{rel_path}"))
}

pub async fn upload_form(
    State(state): State<SharedState>,
    jar: CookieJar,
    multipart: Multipart,
) -> Result<Response> {
    let Ok((admin, _ctx)) = guard(&state, &jar).await? else {
        return Ok(Redirect::to("/manage/login").into_response());
    };
    let incoming = match parse_upload(multipart).await {
        Ok(v) => v,
        Err(e) => return Ok(redirect_msg("/manage/uploads", &e)),
    };
    match store_upload(&state, &admin, incoming).await {
        Ok(_) => Ok(redirect_msg("/manage/uploads", "图片已上传。")),
        Err(e) => Ok(redirect_msg("/manage/uploads", &e)),
    }
}

pub async fn upload_ajax(
    State(state): State<SharedState>,
    jar: CookieJar,
    multipart: Multipart,
) -> Response {
    let admin = match auth::admin_session(&state, &jar).await {
        Some(admin) => admin,
        None => {
            return (
                StatusCode::UNAUTHORIZED,
                Json(json!({"error": "请先登录后台。"})),
            )
                .into_response()
        }
    };
    let incoming = match parse_upload(multipart).await {
        Ok(v) => v,
        Err(e) => return (StatusCode::BAD_REQUEST, Json(json!({"error": e}))).into_response(),
    };
    match store_upload(&state, &admin, incoming).await {
        Ok(url) => Json(json!({"url": url})).into_response(),
        Err(e) => (StatusCode::BAD_REQUEST, Json(json!({"error": e}))).into_response(),
    }
}

pub async fn upload_delete(
    State(state): State<SharedState>,
    jar: CookieJar,
    Path(id): Path<i64>,
    Form(form): Form<CsrfOnly>,
) -> Result<Response> {
    let Ok((admin, _ctx)) = guard(&state, &jar).await? else {
        return Ok(Redirect::to("/manage/login").into_response());
    };
    check_csrf(&admin, &form.csrf)?;
    let row = sqlx::query_as::<_, UploadRow>(
        "SELECT id, path, original_name, mime, size, created_at FROM uploads WHERE id = ?",
    )
    .bind(id)
    .fetch_optional(&state.db)
    .await?;
    if let Some(row) = row {
        sqlx::query("DELETE FROM uploads WHERE id = ?")
            .bind(id)
            .execute(&state.db)
            .await?;
        let path: std::path::PathBuf = row
            .path
            .split('/')
            .filter(|p| !p.is_empty() && *p != ".." && *p != ".")
            .collect();
        let full = state.cfg.uploads_dir().join(path);
        let _ = tokio::fs::remove_file(full).await;
    }
    Ok(redirect_msg("/manage/uploads", "图片已删除。"))
}

/* ------------------------------------------------------------------ */
/* Players                                                             */
/* ------------------------------------------------------------------ */

#[derive(sqlx::FromRow)]
struct AdminPlayerRow {
    id: i64,
    username: String,
    display_name: String,
    coins: i64,
    points: i64,
    level: i64,
    progress: i64,
    created_at: i64,
}

struct AdminPlayerView {
    id: i64,
    username: String,
    display_name: String,
    coins: i64,
    points: i64,
    level: i64,
    progress: i64,
    created: String,
}

#[derive(Template)]
#[template(path = "manage/players.html")]
struct PlayersTemplate {
    ctx: Ctx,
    players: Vec<AdminPlayerView>,
    notice: String,
}

pub async fn players(
    State(state): State<SharedState>,
    jar: CookieJar,
    Query(query): Query<HashMap<String, String>>,
) -> Result<Response> {
    let Ok((_admin, ctx)) = guard(&state, &jar).await? else {
        return Ok(Redirect::to("/manage/login").into_response());
    };
    let rows = sqlx::query_as::<_, AdminPlayerRow>(
        "SELECT p.id, p.username, p.display_name, p.coins, p.points, p.level, p.created_at, \
                (SELECT COUNT(*) FROM game_progress g WHERE g.player_id = p.id) AS progress \
         FROM players p ORDER BY p.created_at DESC, p.id DESC",
    )
    .fetch_all(&state.db)
    .await?;
    let players = rows
        .iter()
        .map(|p| AdminPlayerView {
            id: p.id,
            username: p.username.clone(),
            display_name: if p.display_name.trim().is_empty() {
                p.username.clone()
            } else {
                p.display_name.clone()
            },
            coins: p.coins,
            points: p.points,
            level: p.level,
            progress: p.progress,
            created: fmt_datetime(p.created_at),
        })
        .collect();
    Ok(HtmlTemplate(PlayersTemplate {
        ctx,
        players,
        notice: query.get("msg").cloned().unwrap_or_default(),
    })
    .into_response())
}

struct ProgressItem {
    drama_name: String,
    node_desc: String,
    status: String,
    updated: String,
}

#[derive(Template)]
#[template(path = "manage/player_detail.html")]
struct PlayerDetailTemplate {
    ctx: Ctx,
    player: PlayerView,
    progress: Vec<ProgressItem>,
    notice: String,
}

pub async fn player_detail(
    State(state): State<SharedState>,
    jar: CookieJar,
    Path(id): Path<i64>,
    Query(query): Query<HashMap<String, String>>,
) -> Result<Response> {
    let Ok((_admin, ctx)) = guard(&state, &jar).await? else {
        return Ok(Redirect::to("/manage/login").into_response());
    };
    let player = sqlx::query_as::<_, PlayerRow>(
        "SELECT id, username, password_hash, display_name, coins, points, level, \
                last_signin_at, created_at, updated_at FROM players WHERE id = ?",
    )
    .bind(id)
    .fetch_optional(&state.db)
    .await?
    .ok_or(AppError::NotFound)?;
    let rows = sqlx::query_as::<_, (i64, String, String, i64, i64)>(
        "SELECT p.drama_id, COALESCE(d.name, '(已删除)'), p.node_id, p.status, p.updated_at \
         FROM game_progress p LEFT JOIN dramas d ON d.id = p.drama_id \
         WHERE p.player_id = ? ORDER BY p.updated_at DESC",
    )
    .bind(id)
    .fetch_all(&state.db)
    .await?;
    let dramas = sqlx::query_as::<_, DramaRow>(
        "SELECT id, name, description, status, script_json, created_at, updated_at FROM dramas",
    )
    .fetch_all(&state.db)
    .await?;
    let mut progress = Vec::new();
    for (drama_id, drama_name, node_id, status, updated_at) in rows {
        let node_desc = dramas
            .iter()
            .find(|d| d.id == drama_id)
            .and_then(|d| crate::game::Script::parse(&d.script_json).ok())
            .and_then(|s| s.node(&node_id).map(|n| n.description.clone()))
            .unwrap_or_else(|| format!("节点 {node_id}"));
        progress.push(ProgressItem {
            drama_name,
            node_desc,
            status: crate::game::status_label(status).to_string(),
            updated: fmt_datetime(updated_at),
        });
    }
    Ok(HtmlTemplate(PlayerDetailTemplate {
        ctx,
        player: PlayerView::from(&player),
        progress,
        notice: query.get("msg").cloned().unwrap_or_default(),
    })
    .into_response())
}

pub async fn player_reset(
    State(state): State<SharedState>,
    jar: CookieJar,
    Path(id): Path<i64>,
    Form(form): Form<CsrfOnly>,
) -> Result<Response> {
    let Ok((admin, _ctx)) = guard(&state, &jar).await? else {
        return Ok(Redirect::to("/manage/login").into_response());
    };
    check_csrf(&admin, &form.csrf)?;
    sqlx::query("DELETE FROM game_progress WHERE player_id = ?")
        .bind(id)
        .execute(&state.db)
        .await?;
    Ok(redirect_msg(
        &format!("/manage/players/{id}"),
        "该玩家的游戏进度已重置。",
    ))
}

pub async fn player_delete(
    State(state): State<SharedState>,
    jar: CookieJar,
    Path(id): Path<i64>,
    Form(form): Form<CsrfOnly>,
) -> Result<Response> {
    let Ok((admin, _ctx)) = guard(&state, &jar).await? else {
        return Ok(Redirect::to("/manage/login").into_response());
    };
    check_csrf(&admin, &form.csrf)?;
    sqlx::query("DELETE FROM players WHERE id = ?")
        .bind(id)
        .execute(&state.db)
        .await?;
    sqlx::query("DELETE FROM sessions WHERE kind = ? AND subject_id = ?")
        .bind(KIND_PLAYER)
        .bind(id)
        .execute(&state.db)
        .await?;
    Ok(redirect_msg("/manage/players", "玩家已删除。"))
}

/* ------------------------------------------------------------------ */
/* Markdown preview                                                    */
/* ------------------------------------------------------------------ */

#[derive(Deserialize)]
pub struct PreviewForm {
    #[serde(default)]
    pub csrf: String,
    #[serde(default)]
    pub body_md: String,
}

pub async fn preview(
    State(state): State<SharedState>,
    jar: CookieJar,
    Form(form): Form<PreviewForm>,
) -> Result<Response> {
    let Ok((admin, _ctx)) = guard(&state, &jar).await? else {
        return Ok((
            StatusCode::UNAUTHORIZED,
            Json(json!({"error": "请先登录后台。"})),
        )
            .into_response());
    };
    check_csrf(&admin, &form.csrf)?;
    Ok(Json(json!({"html": render_markdown(&form.body_md)})).into_response())
}

/* ------------------------------------------------------------------ */
/* Small shared helpers                                                */
/* ------------------------------------------------------------------ */
