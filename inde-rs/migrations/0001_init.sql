-- Settings and editable content
CREATE TABLE settings (
    key   TEXT PRIMARY KEY,
    value TEXT NOT NULL DEFAULT ''
);

CREATE TABLE mottos (
    id         INTEGER PRIMARY KEY AUTOINCREMENT,
    name       TEXT NOT NULL,
    translate  TEXT NOT NULL DEFAULT '',
    created_at INTEGER NOT NULL DEFAULT 0
);

CREATE TABLE posts (
    id           INTEGER PRIMARY KEY AUTOINCREMENT,
    title        TEXT NOT NULL,
    summary      TEXT NOT NULL DEFAULT '',
    tags         TEXT NOT NULL DEFAULT '',
    cover        TEXT NOT NULL DEFAULT '',
    body_md      TEXT NOT NULL DEFAULT '',
    views        INTEGER NOT NULL DEFAULT 0,
    likes        INTEGER NOT NULL DEFAULT 0,
    created_at   INTEGER NOT NULL DEFAULT 0,
    updated_at   INTEGER NOT NULL DEFAULT 0,
    published_at INTEGER NOT NULL DEFAULT 0
);

CREATE TABLE books (
    id           INTEGER PRIMARY KEY AUTOINCREMENT,
    name         TEXT NOT NULL,
    author       TEXT NOT NULL DEFAULT '',
    score        REAL NOT NULL DEFAULT 0,
    summary      TEXT NOT NULL DEFAULT '',
    cover        TEXT NOT NULL DEFAULT '',
    download_url TEXT NOT NULL DEFAULT '',
    content_md   TEXT NOT NULL DEFAULT '',
    views        INTEGER NOT NULL DEFAULT 0,
    created_at   INTEGER NOT NULL DEFAULT 0,
    updated_at   INTEGER NOT NULL DEFAULT 0
);

CREATE TABLE photos (
    id         INTEGER PRIMARY KEY AUTOINCREMENT,
    name       TEXT NOT NULL DEFAULT '',
    url        TEXT NOT NULL,
    sort_order INTEGER NOT NULL DEFAULT 0,
    created_at INTEGER NOT NULL DEFAULT 0
);

CREATE TABLE dramas (
    id          INTEGER PRIMARY KEY AUTOINCREMENT,
    name        TEXT NOT NULL,
    description TEXT NOT NULL DEFAULT '',
    status      INTEGER NOT NULL DEFAULT 0,
    script_json TEXT NOT NULL DEFAULT '',
    created_at  INTEGER NOT NULL DEFAULT 0,
    updated_at  INTEGER NOT NULL DEFAULT 0
);

-- Players
CREATE TABLE players (
    id              INTEGER PRIMARY KEY AUTOINCREMENT,
    username        TEXT NOT NULL UNIQUE,
    password_hash   TEXT NOT NULL,
    display_name    TEXT NOT NULL DEFAULT '',
    coins           INTEGER NOT NULL DEFAULT 0,
    points          INTEGER NOT NULL DEFAULT 0,
    level           INTEGER NOT NULL DEFAULT 0,
    last_signin_at  INTEGER NOT NULL DEFAULT 0,
    created_at      INTEGER NOT NULL DEFAULT 0,
    updated_at      INTEGER NOT NULL DEFAULT 0
);

CREATE TABLE game_progress (
    player_id  INTEGER NOT NULL,
    drama_id   INTEGER NOT NULL,
    node_id    TEXT NOT NULL,
    status     INTEGER NOT NULL DEFAULT 0, -- 0 in progress, 1 finished, 2 interrupted
    updated_at INTEGER NOT NULL DEFAULT 0,
    PRIMARY KEY (player_id, drama_id),
    FOREIGN KEY (player_id) REFERENCES players(id) ON DELETE CASCADE,
    FOREIGN KEY (drama_id) REFERENCES dramas(id) ON DELETE CASCADE
);

-- Login sessions (admin and player)
CREATE TABLE sessions (
    token_hash TEXT PRIMARY KEY,
    kind       TEXT NOT NULL,
    subject_id INTEGER NOT NULL DEFAULT 0,
    csrf       TEXT NOT NULL,
    expires_at INTEGER NOT NULL,
    created_at INTEGER NOT NULL
);

CREATE TABLE uploads (
    id            INTEGER PRIMARY KEY AUTOINCREMENT,
    path          TEXT NOT NULL UNIQUE,
    original_name TEXT NOT NULL DEFAULT '',
    mime          TEXT NOT NULL DEFAULT '',
    size          INTEGER NOT NULL DEFAULT 0,
    created_at    INTEGER NOT NULL DEFAULT 0
);

CREATE INDEX idx_posts_published ON posts (published_at);
CREATE INDEX idx_books_name ON books (name);
CREATE INDEX idx_photos_sort ON photos (sort_order);
CREATE INDEX idx_sessions_expire ON sessions (expires_at);
CREATE INDEX idx_progress_player ON game_progress (player_id);

INSERT INTO settings (key, value) VALUES
    ('site_title', '狂or野 - 你喜欢哪一个我'),
    ('site_subtitle', '你喜欢哪一个我'),
    ('audio_url', '/assets/audio/a.mp3'),
    ('about_html', '<p>这是kyy的个人网站。</p><p>写点字，读点书，偶尔玩一局文字游戏。</p>');

INSERT INTO mottos (name, translate, created_at) VALUES
    ('徒善不足以为政，徒法不足以自行', '', strftime('%s','now'));

INSERT INTO dramas (name, description, status, script_json, created_at, updated_at) VALUES (
    '一号测试',
    '一个小故事2',
    1,
    '{"start":"1-b7a1cd21-3d97-46ab-a002-5dfd8e046c0c","nodes":[{"id":"1-b7a1cd21-3d97-46ab-a002-5dfd8e046c0c","kind":"scene","description":"场景1","choices":[{"description":"不，选我","next":"1-2d951cd0-d258-4e5d-a194-32bcea44e1cd"},{"description":"选我","next":"1-10a4fa8c-4b60-4815-ac67-b620c2c0562b"}]},{"id":"1-2d951cd0-d258-4e5d-a194-32bcea44e1cd","kind":"end","description":"失败","choices":[]},{"id":"1-10a4fa8c-4b60-4815-ac67-b620c2c0562b","kind":"end","description":"成功","choices":[]}]}',
    strftime('%s','now'),
    strftime('%s','now')
);
