mod auth;
mod config;
mod error;
mod game;
mod models;
mod state;
mod util;
mod web;

use crate::config::Config;
use crate::state::{AppState, SharedState};
use axum::extract::{DefaultBodyLimit, Request};
use axum::http::{header, HeaderValue};
use axum::middleware::{self, Next};
use axum::response::Response;
use axum::routing::{get, post};
use axum::Router;
use sqlx::sqlite::{SqliteConnectOptions, SqliteJournalMode, SqlitePoolOptions};
use std::net::SocketAddr;
use std::str::FromStr;
use std::sync::Arc;
use std::time::Duration;
use tower_http::compression::CompressionLayer;
use tower_http::trace::TraceLayer;

#[tokio::main]
async fn main() {
    // 本地调试：优先加载 .env.local，其次 .env。
    // dotenvy 不会覆盖已存在的进程环境变量，因此容器部署（docker compose env_file）行为不变。
    let _ = dotenvy::from_filename(".env.local");
    let _ = dotenvy::dotenv();

    tracing_subscriber::fmt()
        .with_env_filter(
            tracing_subscriber::EnvFilter::try_from_default_env()
                .unwrap_or_else(|_| tracing_subscriber::EnvFilter::new("info")),
        )
        .init();

    if let Err(message) = run().await {
        eprintln!("fatal: {message}");
        std::process::exit(1);
    }
}

async fn open_db_pool(
    db_path: &std::path::Path,
    journal_mode: SqliteJournalMode,
) -> Result<sqlx::SqlitePool, String> {
    let options = SqliteConnectOptions::from_str(&format!(
        "sqlite://{}",
        db_path.to_string_lossy().replace('\\', "/")
    ))
    .map_err(|e| format!("bad database path: {e}"))?
    .create_if_missing(true)
    .journal_mode(journal_mode)
    .foreign_keys(true)
    .busy_timeout(Duration::from_secs(10));

    SqlitePoolOptions::new()
        .max_connections(5)
        .acquire_timeout(Duration::from_secs(15))
        .connect_with(options)
        .await
        .map_err(|e| e.to_string())
}

async fn run() -> Result<(), String> {
    let cfg = Config::from_env()?;

    let admin_password = std::env::var("ADMIN_PASSWORD").unwrap_or_default();
    if admin_password.chars().count() < 8 {
        return Err(
            "ADMIN_PASSWORD is required and must be at least 8 characters long.".to_string(),
        );
    }

    tokio::fs::create_dir_all(&cfg.data_dir)
        .await
        .map_err(|e| format!("cannot create DATA_DIR {:?}: {e}", cfg.data_dir))?;
    tokio::fs::create_dir_all(cfg.uploads_dir())
        .await
        .map_err(|e| format!("cannot create uploads dir: {e}"))?;

    // WAL 需要共享内存锁；Docker Desktop 的 Windows bind mount、部分 SMB/NFS
    // 文件系统不支持，会报 (code: 4618) disk I/O error。此时退回 DELETE 日志模式。
    let db = match open_db_pool(&cfg.db_path(), SqliteJournalMode::Wal).await {
        Ok(db) => db,
        Err(err) => {
            eprintln!(
                "warning: WAL journal mode unavailable ({err}); \
                 falling back to DELETE journal mode"
            );
            open_db_pool(&cfg.db_path(), SqliteJournalMode::Delete)
                .await
                .map_err(|e| format!("cannot open database: {e}"))?
        }
    };

    sqlx::migrate!("./migrations")
        .run(&db)
        .await
        .map_err(|e| format!("migration failed: {e}"))?;

    let admin_hash = auth::hash_password(&admin_password).map_err(|e| e.to_string())?;

    let state: SharedState = Arc::new(AppState {
        db: db.clone(),
        cfg: cfg.clone(),
        admin_hash,
        login_fails: Default::default(),
    });

    let cleanup_db = db.clone();
    tokio::spawn(async move {
        let mut ticker = tokio::time::interval(Duration::from_secs(3600));
        loop {
            ticker.tick().await;
            if let Err(e) = auth::cleanup_sessions(&cleanup_db).await {
                tracing::warn!("session cleanup failed: {e}");
            }
        }
    });

    let app = router(state);

    let addr = SocketAddr::from(([0, 0, 0, 0], cfg.port));
    let listener = tokio::net::TcpListener::bind(addr)
        .await
        .map_err(|e| format!("cannot bind {addr}: {e}"))?;
    tracing::info!(
        "inde-rs listening on http://{addr} (public URL: {})",
        cfg.base_url
    );

    axum::serve(
        listener,
        app.into_make_service_with_connect_info::<SocketAddr>(),
    )
    .with_graceful_shutdown(shutdown_signal())
    .await
    .map_err(|e| format!("server error: {e}"))?;

    Ok(())
}

fn router(state: SharedState) -> Router {
    let manage = Router::new()
        .route("/", get(web::manage::dashboard))
        .route(
            "/login",
            get(web::manage::login_page).post(web::manage::login),
        )
        .route("/logout", post(web::manage::logout))
        .route(
            "/posts",
            get(web::manage::posts).post(web::manage::post_create),
        )
        .route("/posts/new", get(web::manage::post_new_page))
        .route("/posts/{id}/edit", get(web::manage::post_edit_page))
        .route("/posts/{id}", post(web::manage::post_update))
        .route("/posts/{id}/delete", post(web::manage::post_delete))
        .route(
            "/books",
            get(web::manage::books).post(web::manage::book_create),
        )
        .route("/books/new", get(web::manage::book_new_page))
        .route("/books/{id}/edit", get(web::manage::book_edit_page))
        .route("/books/{id}", post(web::manage::book_update))
        .route("/books/{id}/delete", post(web::manage::book_delete))
        .route(
            "/photos",
            get(web::manage::photos).post(web::manage::photo_create),
        )
        .route("/photos/new", get(web::manage::photo_new_page))
        .route("/photos/{id}/edit", get(web::manage::photo_edit_page))
        .route("/photos/{id}", post(web::manage::photo_update))
        .route("/photos/{id}/delete", post(web::manage::photo_delete))
        .route(
            "/dramas",
            get(web::manage::dramas).post(web::manage::drama_create),
        )
        .route("/dramas/new", get(web::manage::drama_new_page))
        .route("/dramas/{id}/edit", get(web::manage::drama_edit_page))
        .route("/dramas/{id}", post(web::manage::drama_update))
        .route("/dramas/{id}/delete", post(web::manage::drama_delete))
        .route("/settings", get(web::manage::settings_page))
        .route("/settings/save", post(web::manage::settings_save))
        .route("/mottos", post(web::manage::motto_add))
        .route("/mottos/{id}/delete", post(web::manage::motto_delete))
        .route("/uploads", get(web::manage::uploads_page))
        .route("/uploads/upload", post(web::manage::upload_form))
        .route("/uploads/{id}/delete", post(web::manage::upload_delete))
        .route("/upload", post(web::manage::upload_ajax))
        .route("/players", get(web::manage::players))
        .route("/players/{id}", get(web::manage::player_detail))
        .route("/players/{id}/reset", post(web::manage::player_reset))
        .route("/players/{id}/delete", post(web::manage::player_delete))
        .route("/preview", post(web::manage::preview));

    let game = Router::new()
        .route("/play", get(web::player::play))
        .route(
            "/login",
            get(web::player::login_page).post(web::player::login),
        )
        .route(
            "/register",
            get(web::player::register_page).post(web::player::register),
        )
        .route("/logout", post(web::player::logout))
        .route("/signin", post(web::player::signin))
        .route("/rename", post(web::player::rename))
        .route("/levelup", post(web::player::levelup))
        .route("/dramas/{id}/enter", post(web::player::enter))
        .route("/dramas/{id}/exit", post(web::player::exit))
        .route("/dramas/{id}/reset", post(web::player::reset))
        .route("/dramas/{id}/choose/{index}", post(web::player::choose));

    Router::new()
        .route("/", get(web::public::home))
        .route("/about", get(web::public::about))
        .route("/blog", get(web::public::blog_list))
        .route("/blog/p/{id}", get(web::public::blog_post))
        .route("/books", get(web::public::books_list))
        .route("/b/{id}", get(web::public::book_detail))
        .route("/photos", get(web::public::photos))
        .route("/game", get(web::public::game_landing))
        .route("/healthz", get(web::public::healthz))
        .route("/assets/{*path}", get(web::assets::asset))
        .route("/uploads/{*path}", get(web::assets::upload_file))
        .nest("/game", game)
        .nest("/manage", manage)
        .fallback(web::public::not_found)
        .layer(DefaultBodyLimit::max(12 * 1024 * 1024))
        .layer(CompressionLayer::new())
        .layer(TraceLayer::new_for_http())
        .layer(middleware::from_fn(security_headers))
        .with_state(state)
}

async fn security_headers(req: Request, next: Next) -> Response {
    let mut response = next.run(req).await;
    let headers = response.headers_mut();
    headers.insert(
        header::X_CONTENT_TYPE_OPTIONS,
        HeaderValue::from_static("nosniff"),
    );
    headers.insert(header::X_FRAME_OPTIONS, HeaderValue::from_static("DENY"));
    headers.insert(
        header::REFERRER_POLICY,
        HeaderValue::from_static("strict-origin-when-cross-origin"),
    );
    headers.insert(
        header::CONTENT_SECURITY_POLICY,
        HeaderValue::from_static(
            "default-src 'self'; img-src 'self' data: https:; style-src 'self' 'unsafe-inline' https:; \
             script-src 'self' 'unsafe-inline'; media-src 'self'; connect-src 'self'; \
             frame-ancestors 'none'; base-uri 'self'; form-action 'self'",
        ),
    );
    response
}

async fn shutdown_signal() {
    let ctrl_c = async {
        let _ = tokio::signal::ctrl_c().await;
    };

    #[cfg(unix)]
    let terminate = async {
        if let Ok(mut signal) =
            tokio::signal::unix::signal(tokio::signal::unix::SignalKind::terminate())
        {
            signal.recv().await;
        }
    };

    #[cfg(not(unix))]
    let terminate = std::future::pending::<()>();

    tokio::select! {
        _ = ctrl_c => {},
        _ = terminate => {},
    }
    tracing::info!("shutting down");
}
