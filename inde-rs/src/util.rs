use chrono::{FixedOffset, TimeZone};
use std::sync::OnceLock;

pub fn now_ts() -> i64 {
    chrono::Utc::now().timestamp()
}

fn tz() -> &'static FixedOffset {
    static TZ: OnceLock<FixedOffset> = OnceLock::new();
    TZ.get_or_init(|| FixedOffset::east_opt(8 * 3600).expect("valid +08:00 offset"))
}

pub fn fmt_date(ts: i64) -> String {
    if ts <= 0 {
        return String::new();
    }
    match tz().timestamp_opt(ts, 0).single() {
        Some(dt) => dt.format("%Y-%m-%d").to_string(),
        None => String::new(),
    }
}

pub fn fmt_datetime(ts: i64) -> String {
    if ts <= 0 {
        return String::new();
    }
    match tz().timestamp_opt(ts, 0).single() {
        Some(dt) => dt.format("%Y-%m-%d %H:%M").to_string(),
        None => String::new(),
    }
}

pub fn year_now() -> i64 {
    tz().timestamp_opt(now_ts(), 0)
        .single()
        .map(|dt| chrono::Datelike::year(&dt) as i64)
        .unwrap_or(2026)
}

/// Truncate by characters (not bytes), appending an ellipsis when cut.
pub fn truncate(text: &str, max_chars: usize) -> String {
    let mut out = String::with_capacity(max_chars + 1);
    for (i, ch) in text.chars().enumerate() {
        if i >= max_chars {
            out.push('…');
            break;
        }
        out.push(ch);
    }
    out
}

/// Split a comma separated tag field into clean, deduplicated tags.
pub fn split_tags(raw: &str) -> Vec<String> {
    let mut tags: Vec<String> = Vec::new();
    for part in raw.split(',') {
        let t = part.trim();
        if !t.is_empty() && !tags.iter().any(|x| x == t) {
            tags.push(t.to_string());
        }
    }
    tags
}

pub fn normalize_tags(raw: &str) -> String {
    split_tags(raw).join(",")
}
