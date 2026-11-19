use std::env;
use std::path::PathBuf;

#[derive(Clone, Debug)]
pub struct Config {
    /// Normalized base URL, e.g. `https://game.example.com` (no trailing slash).
    pub base_url: String,
    pub port: u16,
    pub data_dir: PathBuf,
    pub cookie_secure: bool,
    pub allow_registration: bool,
}

impl Config {
    pub fn from_env() -> Result<Self, String> {
        let domain = env::var("DOMAIN").unwrap_or_default().trim().to_string();
        if domain.is_empty() {
            return Err(
                "DOMAIN is required, e.g. DOMAIN=game.example.com or DOMAIN=https://game.example.com"
                    .to_string(),
            );
        }

        let base_url = normalize_base_url(&domain)?;
        let port = env::var("PORT")
            .ok()
            .and_then(|v| v.parse::<u16>().ok())
            .unwrap_or(8080);
        let data_dir = PathBuf::from(
            env::var("DATA_DIR")
                .ok()
                .filter(|v| !v.trim().is_empty())
                .unwrap_or_else(|| "/data".to_string()),
        );
        let cookie_secure = env::var("COOKIE_SECURE")
            .map(|v| matches!(v.trim().to_ascii_lowercase().as_str(), "1" | "true" | "yes"))
            .unwrap_or_else(|_| base_url.starts_with("https://"));
        let allow_registration = env::var("ALLOW_REGISTRATION")
            .map(|v| !matches!(v.trim().to_ascii_lowercase().as_str(), "0" | "false" | "no"))
            .unwrap_or(true);

        Ok(Self {
            base_url,
            port,
            data_dir,
            cookie_secure,
            allow_registration,
        })
    }

    pub fn db_path(&self) -> PathBuf {
        self.data_dir.join("inde.db")
    }

    pub fn uploads_dir(&self) -> PathBuf {
        self.data_dir.join("uploads")
    }
}

fn normalize_base_url(input: &str) -> Result<String, String> {
    let input = input.trim().trim_end_matches('/');
    let with_scheme = if input.starts_with("http://") || input.starts_with("https://") {
        input.to_string()
    } else {
        format!("https://{input}")
    };
    let rest = with_scheme
        .strip_prefix("https://")
        .or_else(|| with_scheme.strip_prefix("http://"))
        .unwrap_or_default();
    let valid = !rest.is_empty()
        && !rest.contains('/')
        && !rest.contains(char::is_whitespace)
        && !rest.contains(['@', '?', '#'])
        && match rest.rsplit_once(':') {
            Some((host, port)) => {
                !host.is_empty()
                    && !host.contains(':')
                    && port.chars().all(|c| c.is_ascii_digit())
                    && port.parse::<u16>().is_ok_and(|p| p > 0)
            }
            None => true,
        };
    if !valid {
        return Err(format!(
            "DOMAIN looks invalid: '{input}'. Use a hostname such as game.example.com"
        ));
    }
    Ok(with_scheme)
}

#[cfg(test)]
mod tests {
    use super::normalize_base_url;

    #[test]
    fn accepts_hosts_ports_and_localhost() {
        assert_eq!(
            normalize_base_url("game.example.com").unwrap(),
            "https://game.example.com"
        );
        assert_eq!(
            normalize_base_url("https://ky.oooc.ink:1666/").unwrap(),
            "https://ky.oooc.ink:1666"
        );
        assert_eq!(
            normalize_base_url("http://localhost:18080").unwrap(),
            "http://localhost:18080"
        );
        assert_eq!(
            normalize_base_url("192.168.1.10:8080").unwrap(),
            "https://192.168.1.10:8080"
        );
    }

    #[test]
    fn rejects_paths_and_bad_ports() {
        assert!(normalize_base_url("https://example.com/path").is_err());
        assert!(normalize_base_url("https://example.com:0").is_err());
        assert!(normalize_base_url("example.com:abc").is_err());
        assert!(normalize_base_url("").is_err());
    }
}
