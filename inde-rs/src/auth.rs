use crate::error::{AppError, Result};
use crate::state::AppState;
use crate::util::now_ts;
use argon2::password_hash::{PasswordHash, PasswordHasher, SaltString};
use argon2::{Argon2, PasswordVerifier};
use axum_extra::extract::cookie::{Cookie, CookieJar, SameSite};
use base64::Engine;
use rand::RngCore;
use sha2::{Digest, Sha256};
use sqlx::SqlitePool;
use time::Duration;

pub const KIND_ADMIN: &str = "admin";
pub const KIND_PLAYER: &str = "player";
pub const COOKIE_ADMIN: &str = "inde_admin";
pub const COOKIE_PLAYER: &str = "inde_player";

pub fn random_bytes(n: usize) -> Vec<u8> {
    let mut buf = vec![0u8; n];
    rand::rngs::OsRng.fill_bytes(&mut buf);
    buf
}

pub fn random_token(n: usize) -> String {
    base64::engine::general_purpose::URL_SAFE_NO_PAD.encode(random_bytes(n))
}

pub fn sha256_hex(input: &str) -> String {
    let digest = Sha256::digest(input.as_bytes());
    digest.iter().map(|b| format!("{b:02x}")).collect()
}

pub fn hash_password(password: &str) -> Result<String> {
    let salt_bytes = random_bytes(16);
    let salt = SaltString::encode_b64(&salt_bytes)
        .map_err(|e| AppError::bad(format!("salt error: {e}")))?;
    Argon2::default()
        .hash_password(password.as_bytes(), &salt)
        .map(|h| h.to_string())
        .map_err(|e| AppError::bad(format!("hash error: {e}")))
}

pub fn verify_password(password_hash: &str, password: &str) -> bool {
    match PasswordHash::new(password_hash) {
        Ok(parsed) => Argon2::default()
            .verify_password(password.as_bytes(), &parsed)
            .is_ok(),
        Err(_) => false,
    }
}

#[derive(Debug, Clone, sqlx::FromRow)]
pub struct Session {
    pub token_hash: String,
    pub kind: String,
    pub subject_id: i64,
    pub csrf: String,
    pub expires_at: i64,
}

/// Create a DB-backed session. Returns `(raw_token, csrf_token)`.
pub async fn create_session(
    db: &SqlitePool,
    kind: &str,
    subject_id: i64,
    ttl_secs: i64,
) -> Result<(String, String)> {
    let token = random_token(32);
    let csrf = random_token(32);
    let token_hash = sha256_hex(&token);
    let now = now_ts();
    sqlx::query(
        "INSERT INTO sessions (token_hash, kind, subject_id, csrf, expires_at, created_at) \
         VALUES (?, ?, ?, ?, ?, ?)",
    )
    .bind(&token_hash)
    .bind(kind)
    .bind(subject_id)
    .bind(&csrf)
    .bind(now + ttl_secs)
    .bind(now)
    .execute(db)
    .await?;
    Ok((token, csrf))
}

pub async fn find_session(db: &SqlitePool, token: &str) -> Result<Option<Session>> {
    let token_hash = sha256_hex(token);
    let session = sqlx::query_as::<_, Session>(
        "SELECT token_hash, kind, subject_id, csrf, expires_at FROM sessions WHERE token_hash = ?",
    )
    .bind(&token_hash)
    .fetch_optional(db)
    .await?;
    match session {
        Some(s) if s.expires_at > now_ts() => Ok(Some(s)),
        Some(s) => {
            sqlx::query("DELETE FROM sessions WHERE token_hash = ?")
                .bind(&s.token_hash)
                .execute(db)
                .await?;
            Ok(None)
        }
        None => Ok(None),
    }
}

pub async fn delete_session(db: &SqlitePool, token: &str) -> Result<()> {
    let token_hash = sha256_hex(token);
    sqlx::query("DELETE FROM sessions WHERE token_hash = ?")
        .bind(token_hash)
        .execute(db)
        .await?;
    Ok(())
}

pub async fn cleanup_sessions(db: &SqlitePool) -> Result<u64> {
    let res = sqlx::query("DELETE FROM sessions WHERE expires_at <= ?")
        .bind(now_ts())
        .execute(db)
        .await?;
    Ok(res.rows_affected())
}

pub fn token_from_jar(jar: &CookieJar, name: &str) -> Option<String> {
    jar.get(name)
        .map(|c| c.value().to_string())
        .filter(|v| !v.is_empty())
}

pub fn session_cookie(state: &AppState, name: &str, token: &str, max_age: i64) -> Cookie<'static> {
    Cookie::build((name.to_string(), token.to_string()))
        .path("/")
        .http_only(true)
        .same_site(SameSite::Lax)
        .secure(state.cfg.cookie_secure)
        .max_age(Duration::seconds(max_age))
        .build()
}

pub fn clear_cookie(state: &AppState, name: &str) -> Cookie<'static> {
    Cookie::build((name.to_string(), String::new()))
        .path("/")
        .http_only(true)
        .same_site(SameSite::Lax)
        .secure(state.cfg.cookie_secure)
        .max_age(Duration::seconds(0))
        .build()
}

pub async fn admin_session(state: &AppState, jar: &CookieJar) -> Option<Session> {
    let token = token_from_jar(jar, COOKIE_ADMIN)?;
    match find_session(&state.db, &token).await {
        Ok(Some(s)) if s.kind == KIND_ADMIN => Some(s),
        _ => None,
    }
}

pub async fn player_session(state: &AppState, jar: &CookieJar) -> Option<Session> {
    let token = token_from_jar(jar, COOKIE_PLAYER)?;
    match find_session(&state.db, &token).await {
        Ok(Some(s)) if s.kind == KIND_PLAYER => Some(s),
        _ => None,
    }
}

/// Constant-time comparison for CSRF tokens.
pub fn csrf_matches(expected: &str, provided: &str) -> bool {
    if expected.is_empty() || provided.is_empty() {
        return false;
    }
    let a = expected.as_bytes();
    let b = provided.as_bytes();
    if a.len() != b.len() {
        return false;
    }
    let mut diff = 0u8;
    for (x, y) in a.iter().zip(b.iter()) {
        diff |= x ^ y;
    }
    diff == 0
}

pub fn ip_key(addr: &std::net::SocketAddr) -> String {
    addr.ip().to_string()
}

/// Returns true when the IP is currently locked out.
pub fn login_locked(state: &AppState, key: &str) -> bool {
    let map = state.login_fails.lock().unwrap();
    map.get(key)
        .map(|f| f.locked_until > now_ts())
        .unwrap_or(false)
}

pub fn login_succeeded(state: &AppState, key: &str) {
    state.login_fails.lock().unwrap().remove(key);
}

/// Record one failure; returns true if this failure triggered a lockout.
pub fn login_failed(state: &AppState, key: &str) -> bool {
    let mut map = state.login_fails.lock().unwrap();
    let entry = map.entry(key.to_string()).or_default();
    if entry.locked_until > now_ts() {
        return true;
    }
    entry.count += 1;
    if entry.count >= 5 {
        entry.count = 0;
        entry.locked_until = now_ts() + 60;
        return true;
    }
    false
}
