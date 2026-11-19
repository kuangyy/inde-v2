use crate::error::{AppError, Result};
use crate::models::{
    load_settings, pagination, sanitize_html, setting, urlencode, BookCard, BookRow, BookView, Ctx,
    Pagination, PhotoCard, PhotoRow, PostCard, PostRow, PostView,
};
use crate::state::SharedState;
use crate::web::{build_ctx, page_param, qparam, qs_pair, HtmlTemplate};
use askama::Template;
use axum::extract::{Path, Query, State};
use axum::http::StatusCode;
use axum::response::{IntoResponse, Response};
use axum_extra::extract::CookieJar;
use sqlx::{QueryBuilder, Sqlite};
use std::collections::HashMap;

fn like_pattern(q: &str) -> String {
    let cleaned: String = q.chars().filter(|c| *c != '%' && *c != '_').collect();
    format!("%{}%", cleaned)
}

#[derive(Template)]
#[template(path = "home.html")]
struct HomeTemplate {
    ctx: Ctx,
    motto: String,
    motto_translate: String,
    audio_url: String,
}

pub async fn home(State(state): State<SharedState>, jar: CookieJar) -> Result<Response> {
    let ctx = build_ctx(&state, &jar).await;
    let settings = load_settings(&state.db).await?;
    let motto: Option<(String, String)> =
        sqlx::query_as("SELECT name, translate FROM mottos ORDER BY RANDOM() LIMIT 1")
            .fetch_optional(&state.db)
            .await?;
    let (motto, motto_translate) = motto.unwrap_or_default();
    Ok(HtmlTemplate(HomeTemplate {
        ctx,
        motto,
        motto_translate,
        audio_url: setting(&settings, "audio_url", "/assets/audio/a.mp3"),
    })
    .into_response())
}

#[derive(Template)]
#[template(path = "about.html")]
struct AboutTemplate {
    ctx: Ctx,
    about_html: String,
}

pub async fn about(State(state): State<SharedState>, jar: CookieJar) -> Result<Response> {
    let ctx = build_ctx(&state, &jar).await;
    let settings = load_settings(&state.db).await?;
    Ok(HtmlTemplate(AboutTemplate {
        ctx,
        about_html: sanitize_html(&setting(
            &settings,
            "about_html",
            "<p>这是kyy的个人网站。</p>",
        )),
    })
    .into_response())
}

#[derive(Clone)]
pub struct TagChip {
    pub name: String,
    pub count: i64,
    pub url: String,
}

#[derive(Template)]
#[template(path = "blog_list.html")]
struct BlogListTemplate {
    ctx: Ctx,
    posts: Vec<PostCard>,
    q: String,
    tag: String,
    qs: String,
    has_qs: bool,
    tags: Vec<TagChip>,
    pag: Pagination,
}

fn apply_post_filters(qb: &mut QueryBuilder<'_, Sqlite>, q: &str, tag: &str) {
    if !q.is_empty() {
        qb.push(" AND (title LIKE ")
            .push_bind(like_pattern(q))
            .push(" OR summary LIKE ")
            .push_bind(like_pattern(q))
            .push(" OR body_md LIKE ")
            .push_bind(like_pattern(q))
            .push(")");
    }
    if !tag.is_empty() {
        qb.push(" AND instr(',' || tags || ',', ',' || ")
            .push_bind(tag.to_string())
            .push(" || ',') > 0");
    }
}

pub async fn blog_list(
    State(state): State<SharedState>,
    jar: CookieJar,
    Query(query): Query<HashMap<String, String>>,
) -> Result<Response> {
    let ctx = build_ctx(&state, &jar).await;
    let q = qparam(&query, "q");
    let tag = qparam(&query, "tag");
    let page = page_param(&query);
    let per_page = 8i64;

    let mut count_qb =
        QueryBuilder::<Sqlite>::new("SELECT COUNT(*) FROM posts WHERE published_at > 0");
    apply_post_filters(&mut count_qb, &q, &tag);
    let total: i64 = count_qb.build_query_scalar().fetch_one(&state.db).await?;
    let pag = pagination(page, total, per_page);

    let mut rows_qb = QueryBuilder::<Sqlite>::new(
        "SELECT id, title, summary, tags, cover, body_md, views, likes, created_at, updated_at, published_at \
         FROM posts WHERE published_at > 0",
    );
    apply_post_filters(&mut rows_qb, &q, &tag);
    rows_qb
        .push(" ORDER BY published_at DESC LIMIT ")
        .push_bind(per_page)
        .push(" OFFSET ")
        .push_bind((pag.page - 1) * per_page);
    let rows: Vec<PostRow> = rows_qb.build_query_as().fetch_all(&state.db).await?;
    let posts: Vec<PostCard> = rows.iter().map(PostCard::from).collect();

    let tag_rows: Vec<(String,)> = sqlx::query_as("SELECT tags FROM posts WHERE published_at > 0")
        .fetch_all(&state.db)
        .await?;
    let mut counts: HashMap<String, i64> = HashMap::new();
    for (raw,) in tag_rows {
        for t in crate::util::split_tags(&raw) {
            *counts.entry(t).or_insert(0) += 1;
        }
    }
    let mut tags: Vec<TagChip> = counts
        .into_iter()
        .map(|(name, count)| TagChip {
            url: format!("/blog?tag={}", urlencode(&name)),
            name,
            count,
        })
        .collect();
    tags.sort_by(|a, b| b.count.cmp(&a.count).then_with(|| a.name.cmp(&b.name)));
    tags.truncate(30);

    let qs = qs_pair(&q, &tag);
    Ok(HtmlTemplate(BlogListTemplate {
        ctx,
        posts,
        q,
        tag,
        has_qs: !qs.is_empty(),
        qs,
        tags,
        pag,
    })
    .into_response())
}

#[derive(Template)]
#[template(path = "blog_post.html")]
struct BlogPostTemplate {
    ctx: Ctx,
    post: PostView,
}

pub async fn blog_post(
    State(state): State<SharedState>,
    jar: CookieJar,
    Path(id): Path<i64>,
) -> Result<Response> {
    let ctx = build_ctx(&state, &jar).await;
    let row = sqlx::query_as::<_, PostRow>(
        "SELECT id, title, summary, tags, cover, body_md, views, likes, created_at, updated_at, published_at \
         FROM posts WHERE id = ? AND published_at > 0",
    )
    .bind(id)
    .fetch_optional(&state.db)
    .await?
    .ok_or(AppError::NotFound)?;
    sqlx::query("UPDATE posts SET views = views + 1 WHERE id = ?")
        .bind(id)
        .execute(&state.db)
        .await?;
    let mut post = PostView::from(&row);
    post.views += 1;
    Ok(HtmlTemplate(BlogPostTemplate { ctx, post }).into_response())
}

#[derive(Template)]
#[template(path = "books.html")]
struct BooksTemplate {
    ctx: Ctx,
    books: Vec<BookCard>,
    q: String,
    qs: String,
    has_qs: bool,
    pag: Pagination,
}

pub async fn books_list(
    State(state): State<SharedState>,
    jar: CookieJar,
    Query(query): Query<HashMap<String, String>>,
) -> Result<Response> {
    let ctx = build_ctx(&state, &jar).await;
    let q = qparam(&query, "q");
    let page = page_param(&query);
    let per_page = 12i64;

    let mut count_qb = QueryBuilder::<Sqlite>::new("SELECT COUNT(*) FROM books WHERE 1 = 1");
    if !q.is_empty() {
        count_qb
            .push(" AND (name LIKE ")
            .push_bind(like_pattern(&q))
            .push(" OR author LIKE ")
            .push_bind(like_pattern(&q))
            .push(" OR summary LIKE ")
            .push_bind(like_pattern(&q))
            .push(")");
    }
    let total: i64 = count_qb.build_query_scalar().fetch_one(&state.db).await?;
    let pag = pagination(page, total, per_page);

    let mut rows_qb = QueryBuilder::<Sqlite>::new(
        "SELECT id, name, author, score, summary, cover, download_url, content_md, views, created_at, updated_at \
         FROM books WHERE 1 = 1",
    );
    if !q.is_empty() {
        rows_qb
            .push(" AND (name LIKE ")
            .push_bind(like_pattern(&q))
            .push(" OR author LIKE ")
            .push_bind(like_pattern(&q))
            .push(" OR summary LIKE ")
            .push_bind(like_pattern(&q))
            .push(")");
    }
    rows_qb
        .push(" ORDER BY created_at DESC LIMIT ")
        .push_bind(per_page)
        .push(" OFFSET ")
        .push_bind((pag.page - 1) * per_page);
    let rows: Vec<BookRow> = rows_qb.build_query_as().fetch_all(&state.db).await?;
    let books: Vec<BookCard> = rows.iter().map(BookCard::from).collect();
    let qs = if q.is_empty() {
        String::new()
    } else {
        format!("q={}", urlencode(&q))
    };
    Ok(HtmlTemplate(BooksTemplate {
        ctx,
        books,
        q,
        has_qs: !qs.is_empty(),
        qs,
        pag,
    })
    .into_response())
}

#[derive(Template)]
#[template(path = "book_detail.html")]
struct BookDetailTemplate {
    ctx: Ctx,
    book: BookView,
}

pub async fn book_detail(
    State(state): State<SharedState>,
    jar: CookieJar,
    Path(id): Path<i64>,
) -> Result<Response> {
    let ctx = build_ctx(&state, &jar).await;
    let row = sqlx::query_as::<_, BookRow>(
        "SELECT id, name, author, score, summary, cover, download_url, content_md, views, created_at, updated_at \
         FROM books WHERE id = ?",
    )
    .bind(id)
    .fetch_optional(&state.db)
    .await?
    .ok_or(AppError::NotFound)?;
    sqlx::query("UPDATE books SET views = views + 1 WHERE id = ?")
        .bind(id)
        .execute(&state.db)
        .await?;
    let mut book = BookView::from(&row);
    book.views += 1;
    Ok(HtmlTemplate(BookDetailTemplate { ctx, book }).into_response())
}

#[derive(Template)]
#[template(path = "photos.html")]
struct PhotosTemplate {
    ctx: Ctx,
    photos: Vec<PhotoCard>,
    pag: Pagination,
}

pub async fn photos(
    State(state): State<SharedState>,
    jar: CookieJar,
    Query(query): Query<HashMap<String, String>>,
) -> Result<Response> {
    let ctx = build_ctx(&state, &jar).await;
    let page = page_param(&query);
    let per_page = 24i64;
    let total: i64 = sqlx::query_scalar("SELECT COUNT(*) FROM photos")
        .fetch_one(&state.db)
        .await?;
    let pag = pagination(page, total, per_page);
    let rows: Vec<PhotoRow> = sqlx::query_as::<_, PhotoRow>(
        "SELECT id, name, url, sort_order, created_at FROM photos \
         ORDER BY sort_order ASC, created_at DESC LIMIT ? OFFSET ?",
    )
    .bind(per_page)
    .bind((pag.page - 1) * per_page)
    .fetch_all(&state.db)
    .await?;
    let photos: Vec<PhotoCard> = rows.iter().map(PhotoCard::from).collect();
    Ok(HtmlTemplate(PhotosTemplate { ctx, photos, pag }).into_response())
}

#[derive(Template)]
#[template(path = "game_landing.html")]
struct GameLandingTemplate {
    ctx: Ctx,
}

pub async fn game_landing(State(state): State<SharedState>, jar: CookieJar) -> Result<Response> {
    let ctx = build_ctx(&state, &jar).await;
    Ok(HtmlTemplate(GameLandingTemplate { ctx }).into_response())
}

pub async fn healthz() -> impl IntoResponse {
    (StatusCode::OK, "ok")
}

#[derive(Template)]
#[template(path = "not_found.html")]
struct NotFoundTemplate {
    ctx: Ctx,
}

pub async fn not_found(State(state): State<SharedState>, jar: CookieJar) -> Response {
    let ctx = build_ctx(&state, &jar).await;
    let mut response = HtmlTemplate(NotFoundTemplate { ctx }).into_response();
    *response.status_mut() = StatusCode::NOT_FOUND;
    response
}
