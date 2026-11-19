use axum::http::StatusCode;
use axum::response::{Html, IntoResponse, Response};
use thiserror::Error;

pub type Result<T> = std::result::Result<T, AppError>;

#[derive(Debug, Error)]
pub enum AppError {
    #[error("数据库错误")]
    Db(#[from] sqlx::Error),
    #[error("模板错误")]
    Template(#[from] askama::Error),
    #[error("{0}")]
    BadRequest(String),
    #[error("页面不存在")]
    NotFound,
    #[error("没有权限")]
    Forbidden,
    #[error("文件系统错误")]
    Io(#[from] std::io::Error),
}

impl AppError {
    pub fn bad(msg: impl Into<String>) -> Self {
        AppError::BadRequest(msg.into())
    }
}

fn escape(text: &str) -> String {
    text.replace('&', "&amp;")
        .replace('<', "&lt;")
        .replace('>', "&gt;")
        .replace('"', "&quot;")
}

impl IntoResponse for AppError {
    fn into_response(self) -> Response {
        let status = match &self {
            AppError::NotFound => StatusCode::NOT_FOUND,
            AppError::Forbidden => StatusCode::FORBIDDEN,
            AppError::BadRequest(_) => StatusCode::BAD_REQUEST,
            AppError::Db(_) | AppError::Template(_) | AppError::Io(_) => {
                StatusCode::INTERNAL_SERVER_ERROR
            }
        };
        if status == StatusCode::INTERNAL_SERVER_ERROR {
            match &self {
                AppError::Db(e) => tracing::error!(error = %e, "database error"),
                AppError::Template(e) => tracing::error!(error = %e, "template error"),
                AppError::Io(e) => tracing::error!(error = %e, "io error"),
                _ => {}
            }
        }
        let message = escape(&self.to_string());
        let body = format!(
            "<!doctype html><html lang=\"zh-CN\"><head><meta charset=\"utf-8\"><title>出错了</title>\
             <style>body{{font-family:system-ui,sans-serif;background:#1d1f24;color:#e8e8e8;display:flex;\
             align-items:center;justify-content:center;height:100vh;margin:0}}\
             main{{text-align:center}}a{{color:#7fdbff}}</style></head>\
             <body><main><h1>出错了</h1><p>{message}</p><p><a href=\"/\">回到首页</a></p></main></body></html>"
        );
        (status, Html(body)).into_response()
    }
}
