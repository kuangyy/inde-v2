use crate::error::{AppError, Result};
use axum::body::Body;
use axum::extract::{Path, State};
use axum::http::{header, HeaderValue, StatusCode};
use axum::response::{IntoResponse, Response};
use rust_embed::RustEmbed;

#[derive(RustEmbed)]
#[folder = "assets/"]
struct Assets;

/// Only allow safe relative paths made of alphanumerics, `-`, `_`, `.` and `/`.
fn sanitize_rel_path(raw: &str) -> Result<String> {
    let mut parts = Vec::new();
    for part in raw.split('/') {
        if part.is_empty() || part == "." || part == ".." {
            return Err(AppError::NotFound);
        }
        if !part
            .chars()
            .all(|c| c.is_ascii_alphanumeric() || matches!(c, '-' | '_' | '.'))
        {
            return Err(AppError::NotFound);
        }
        parts.push(part);
    }
    if parts.is_empty() {
        return Err(AppError::NotFound);
    }
    Ok(parts.join("/"))
}

pub async fn asset(Path(path): Path<String>) -> Result<Response> {
    let rel = sanitize_rel_path(&path)?;
    let Some(file) = Assets::get(&rel) else {
        return Err(AppError::NotFound);
    };
    let mime = mime_guess::from_path(&rel).first_or_octet_stream();
    let headers = [
        (
            header::CONTENT_TYPE,
            HeaderValue::from_str(mime.as_ref()).unwrap(),
        ),
        (
            header::CACHE_CONTROL,
            HeaderValue::from_static("public, max-age=86400"),
        ),
        (
            header::X_CONTENT_TYPE_OPTIONS,
            HeaderValue::from_static("nosniff"),
        ),
    ];
    let body = Body::from(file.data.into_owned());
    let mut response = Response::new(body);
    for (name, value) in headers {
        response.headers_mut().insert(name, value);
    }
    *response.status_mut() = StatusCode::OK;
    Ok(response.into_response())
}

pub async fn upload_file(
    State(state): State<crate::state::SharedState>,
    Path(path): Path<String>,
) -> Result<Response> {
    let rel = sanitize_rel_path(&path)?;
    let full = state
        .cfg
        .uploads_dir()
        .join(rel.replace('/', std::path::MAIN_SEPARATOR_STR));
    let data = match tokio::fs::read(&full).await {
        Ok(data) => data,
        Err(_) => return Err(AppError::NotFound),
    };
    let mime = mime_guess::from_path(&full).first_or_octet_stream();
    let headers = [
        (
            header::CONTENT_TYPE,
            HeaderValue::from_str(mime.as_ref()).unwrap(),
        ),
        (
            header::CACHE_CONTROL,
            HeaderValue::from_static("public, max-age=31536000, immutable"),
        ),
        (
            header::X_CONTENT_TYPE_OPTIONS,
            HeaderValue::from_static("nosniff"),
        ),
    ];
    let mut response = Response::new(Body::from(data));
    for (name, value) in headers {
        response.headers_mut().insert(name, value);
    }
    *response.status_mut() = StatusCode::OK;
    Ok(response.into_response())
}
