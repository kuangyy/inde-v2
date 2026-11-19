use crate::config::Config;
use sqlx::SqlitePool;
use std::collections::HashMap;
use std::sync::{Arc, Mutex};

#[derive(Debug, Default)]
pub struct LoginFailure {
    pub count: u32,
    pub locked_until: i64,
}

pub struct AppState {
    pub db: SqlitePool,
    pub cfg: Config,
    /// Argon2 hash of ADMIN_PASSWORD, computed once at startup.
    pub admin_hash: String,
    pub login_fails: Mutex<HashMap<String, LoginFailure>>,
}

pub type SharedState = Arc<AppState>;
