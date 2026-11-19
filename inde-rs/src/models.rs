// Some fields are only read from Askama templates, which dead-code analysis
// cannot see through.
#![allow(dead_code)]

use crate::util::{fmt_date, fmt_datetime, split_tags, truncate};
use pulldown_cmark::{html, Options, Parser};
use sqlx::SqlitePool;
use std::collections::HashMap;

#[derive(Clone, Default)]
pub struct Ctx {
    pub base_url: String,
    pub site_title: String,
    pub is_admin: bool,
    pub player_id: i64,
    pub player_name: String,
    pub csrf: String,
    pub year: i64,
}

#[derive(Debug, Clone, sqlx::FromRow)]
pub struct PostRow {
    pub id: i64,
    pub title: String,
    pub summary: String,
    pub tags: String,
    pub cover: String,
    pub body_md: String,
    pub views: i64,
    pub likes: i64,
    pub created_at: i64,
    pub updated_at: i64,
    pub published_at: i64,
}

#[derive(Debug, Clone, sqlx::FromRow)]
pub struct BookRow {
    pub id: i64,
    pub name: String,
    pub author: String,
    pub score: f64,
    pub summary: String,
    pub cover: String,
    pub download_url: String,
    pub content_md: String,
    pub views: i64,
    pub created_at: i64,
    pub updated_at: i64,
}

#[derive(Debug, Clone, sqlx::FromRow)]
pub struct PhotoRow {
    pub id: i64,
    pub name: String,
    pub url: String,
    pub sort_order: i64,
    pub created_at: i64,
}

#[derive(Debug, Clone, sqlx::FromRow)]
pub struct DramaRow {
    pub id: i64,
    pub name: String,
    pub description: String,
    pub status: i64,
    pub script_json: String,
    pub created_at: i64,
    pub updated_at: i64,
}

#[derive(Debug, Clone, sqlx::FromRow)]
pub struct PlayerRow {
    pub id: i64,
    pub username: String,
    pub password_hash: String,
    pub display_name: String,
    pub coins: i64,
    pub points: i64,
    pub level: i64,
    pub last_signin_at: i64,
    pub created_at: i64,
    pub updated_at: i64,
}

#[derive(Debug, Clone, sqlx::FromRow)]
pub struct ProgressRow {
    pub player_id: i64,
    pub drama_id: i64,
    pub node_id: String,
    pub status: i64,
    pub updated_at: i64,
}

#[derive(Debug, Clone, sqlx::FromRow)]
pub struct UploadRow {
    pub id: i64,
    pub path: String,
    pub original_name: String,
    pub mime: String,
    pub size: i64,
    pub created_at: i64,
}

#[derive(Clone)]
pub struct PostCard {
    pub id: i64,
    pub title: String,
    pub summary: String,
    pub cover: String,
    pub date: String,
    pub tags: Vec<String>,
}

impl From<&PostRow> for PostCard {
    fn from(p: &PostRow) -> Self {
        let summary = if p.summary.trim().is_empty() {
            truncate(&strip_markdown(&p.body_md), 140)
        } else {
            p.summary.clone()
        };
        Self {
            id: p.id,
            title: p.title.clone(),
            summary,
            cover: p.cover.clone(),
            date: fmt_date(p.published_at.max(p.created_at)),
            tags: split_tags(&p.tags),
        }
    }
}

#[derive(Clone)]
pub struct PostView {
    pub id: i64,
    pub title: String,
    pub summary: String,
    pub cover: String,
    pub date: String,
    pub tags: Vec<String>,
    pub body_html: String,
    pub views: i64,
}

impl From<&PostRow> for PostView {
    fn from(p: &PostRow) -> Self {
        let card = PostCard::from(p);
        Self {
            id: card.id,
            title: card.title,
            summary: card.summary,
            cover: card.cover,
            date: card.date,
            tags: card.tags,
            body_html: render_markdown(&p.body_md),
            views: p.views,
        }
    }
}

#[derive(Clone)]
pub struct BookCard {
    pub id: i64,
    pub name: String,
    pub author: String,
    pub score: String,
    pub summary: String,
    pub cover: String,
    pub date: String,
}

impl From<&BookRow> for BookCard {
    fn from(b: &BookRow) -> Self {
        Self {
            id: b.id,
            name: b.name.clone(),
            author: b.author.clone(),
            score: format!("{:.1}", b.score),
            summary: truncate(&b.summary, 160),
            cover: b.cover.clone(),
            date: fmt_date(b.created_at),
        }
    }
}

#[derive(Clone)]
pub struct BookView {
    pub id: i64,
    pub name: String,
    pub author: String,
    pub score: String,
    pub summary: String,
    pub cover: String,
    pub date: String,
    pub download_url: String,
    pub content_html: String,
    pub views: i64,
}

impl From<&BookRow> for BookView {
    fn from(b: &BookRow) -> Self {
        let card = BookCard::from(b);
        Self {
            id: card.id,
            name: card.name,
            author: card.author,
            score: card.score,
            summary: card.summary,
            cover: card.cover,
            date: card.date,
            download_url: b.download_url.clone(),
            content_html: render_markdown(&b.content_md),
            views: b.views,
        }
    }
}

#[derive(Clone)]
pub struct PhotoCard {
    pub id: i64,
    pub name: String,
    pub url: String,
}

impl From<&PhotoRow> for PhotoCard {
    fn from(p: &PhotoRow) -> Self {
        Self {
            id: p.id,
            name: p.name.clone(),
            url: p.url.clone(),
        }
    }
}

#[derive(Clone)]
pub struct PlayerView {
    pub id: i64,
    pub username: String,
    pub display_name: String,
    pub coins: i64,
    pub points: i64,
    pub level: i64,
    pub last_signin: String,
    pub created: String,
}

impl From<&PlayerRow> for PlayerView {
    fn from(p: &PlayerRow) -> Self {
        Self {
            id: p.id,
            username: p.username.clone(),
            display_name: p.display_name.clone(),
            coins: p.coins,
            points: p.points,
            level: p.level,
            last_signin: if p.last_signin_at > 0 {
                fmt_datetime(p.last_signin_at)
            } else {
                "尚未签到".to_string()
            },
            created: fmt_datetime(p.created_at),
        }
    }
}

#[derive(Clone)]
pub struct UploadView {
    pub id: i64,
    pub url: String,
    pub path: String,
    pub original_name: String,
    pub size: String,
    pub created: String,
}

impl From<&UploadRow> for UploadView {
    fn from(u: &UploadRow) -> Self {
        Self {
            id: u.id,
            url: format!("/uploads/{}", u.path),
            path: u.path.clone(),
            original_name: u.original_name.clone(),
            size: format_size(u.size),
            created: fmt_datetime(u.created_at),
        }
    }
}

#[derive(Clone, Default)]
pub struct Pagination {
    pub page: i64,
    pub total_pages: i64,
    pub total: i64,
    pub has_prev: bool,
    pub has_next: bool,
    pub prev_page: i64,
    pub next_page: i64,
}

pub fn pagination(page: i64, total: i64, per_page: i64) -> Pagination {
    let total_pages = if total <= 0 {
        1
    } else {
        (total + per_page - 1) / per_page
    };
    Pagination {
        page,
        total_pages,
        total,
        has_prev: page > 1,
        has_next: page < total_pages,
        prev_page: (page - 1).max(1),
        next_page: (page + 1).min(total_pages),
    }
}

pub async fn load_settings(db: &SqlitePool) -> sqlx::Result<HashMap<String, String>> {
    let rows: Vec<(String, String)> = sqlx::query_as("SELECT key, value FROM settings")
        .fetch_all(db)
        .await?;
    Ok(rows.into_iter().collect())
}

pub fn setting(map: &HashMap<String, String>, key: &str, default: &str) -> String {
    map.get(key)
        .filter(|v| !v.trim().is_empty())
        .cloned()
        .unwrap_or_else(|| default.to_string())
}

pub fn render_markdown(markdown: &str) -> String {
    let mut options = Options::empty();
    options.insert(Options::ENABLE_TABLES);
    options.insert(Options::ENABLE_STRIKETHROUGH);
    options.insert(Options::ENABLE_TASKLISTS);
    options.insert(Options::ENABLE_FOOTNOTES);
    let parser = Parser::new_ext(markdown, options);
    let mut output = String::new();
    html::push_html(&mut output, parser);
    sanitize_html(&output)
}

pub fn sanitize_html(source: &str) -> String {
    ammonia::Builder::default()
        .add_generic_attributes(&["class"])
        .add_tags(&["img", "figure", "figcaption"])
        .link_rel(Some("noopener noreferrer"))
        .clean(source)
        .to_string()
}

pub fn strip_markdown(markdown: &str) -> String {
    let mut out = String::with_capacity(markdown.len());
    let mut in_code = false;
    for line in markdown.lines() {
        let trimmed = line.trim();
        if trimmed.starts_with("```") {
            in_code = !in_code;
            continue;
        }
        if in_code || trimmed.starts_with('#') && trimmed.chars().all(|c| c == '#' || c == ' ') {
            continue;
        }
        let cleaned = trimmed
            .trim_start_matches('#')
            .trim_start_matches(['-', '*', '>', '+'])
            .trim();
        if cleaned.is_empty() {
            continue;
        }
        out.push_str(cleaned);
        out.push(' ');
    }
    out.split_whitespace().collect::<Vec<_>>().join(" ")
}

pub fn format_size(bytes: i64) -> String {
    if bytes >= 1024 * 1024 {
        format!("{:.1} MB", bytes as f64 / (1024.0 * 1024.0))
    } else if bytes >= 1024 {
        format!("{:.1} KB", bytes as f64 / 1024.0)
    } else {
        format!("{bytes} B")
    }
}

pub fn urlencode(value: &str) -> String {
    percent_encoding::utf8_percent_encode(value, percent_encoding::NON_ALPHANUMERIC).to_string()
}
