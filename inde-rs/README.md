# inde-rs

原 `inde-v2-master`（Spring MVC + JSP，2016）的独立 Rust 重写版：保留首页视觉与「逃离剧本」文字游戏，新增密码后台与博客。

**一个二进制 + 一个 `/data` 目录** 即可运行：SQLite 内嵌、静态资源内嵌，不需要 MySQL、Redis、Tomcat、Nginx 或任何外部 API。

## 功能

- **首页**：星空 canvas、时间标题、背景音乐、随机格言、四个入口磁贴（沿用原版 `index.css`）。
- **关于页**：可在后台编辑正文 HTML。
- **博客**：文章列表（搜索、标签、分页）、Markdown 正文渲染、封面图、浏览量；后台 Markdown 编辑器支持图片上传与预览。
- **书籍**：列表（搜索、分页）、详情页；后台增删改。
- **照片墙**：网格展示；后台维护图片条目。
- **逃离剧本**：玩家注册/登录、每日签到（金币/积分 +10，按 Asia/Shanghai 每日 07:00 换日）、改名、积分升级、进入/中断/重置剧本、分支选择、多结局。剧本在后台以 JSON 编辑并严格校验。
- **管理后台**（`/manage`）：文章、书籍、图片墙、剧本、上传文件、玩家、站点设置与格言。
- **上传**：PNG/JPG/WEBP/GIF，≤10MB，UUID 文件名，落在 `/data/uploads/yyyy/mm/`，通过 `/uploads/...` 提供。

## 配置

复制 `.env.example` 为 `.env`：

| 变量 | 必填 | 默认 | 说明 |
|---|---|---|---|
| `DOMAIN` | 是 | — | 对外域名，如 `game.example.com` 或 `https://game.example.com` |
| `ADMIN_PASSWORD` | 是 | — | 后台密码，≥8 位；启动时用 Argon2id 哈希，不落盘 |
| `PORT` | 否 | `18080` | 监听端口 |
| `DATA_DIR` | 否 | `/data` | SQLite 与上传文件目录 |
| `COOKIE_SECURE` | 否 | 跟随 `DOMAIN` 的 scheme | HTTPS 部署保持 `true`；仅 http 访问时设 `false` |
| `ALLOW_REGISTRATION` | 否 | `true` | 关闭玩家注册 |
| `RUST_LOG` | 否 | `info` | 日志级别 |

## 用 Docker 部署（推荐，NAS 通用）

```bash
cp .env.example .env
# 编辑 .env，至少填 DOMAIN 和 ADMIN_PASSWORD
docker compose up -d --build
```

打开 `http://NAS_IP:18080/`（端口即 `.env` 里的 `PORT`）；后台在 `/manage`。

> 同一个 `./data` 只能被一个进程使用：运行容器时不要再启动本地 exe / `cargo run`，
> 否则 SQLite 会报 `(code: 4618) disk I/O error`。
> 若底层文件系统不支持 WAL（Docker Desktop 的 Windows bind mount、部分 SMB/NFS），
> 程序会自动退回 DELETE 日志模式继续运行。

数据都在 `./data`：

```
data/
├── inde.db          # SQLite（WAL 模式）
└── uploads/         # 上传图片
```

备份：停容器后直接打包 `./data`；或在运行中执行
`docker exec inde-rs sh -c 'echo ".backup /data/backup.db" | sqlite3'`（镜像未带 sqlite3 CLI，推荐停机拷贝）。

### 反向代理（可选）

把域名反代到 `NAS_IP:18080` 即可（群晖反向代理 / Nginx Proxy Manager / Caddy 均可）。走 HTTPS 时务必在 `.env` 里设 `COOKIE_SECURE=true`。

Nginx 示例：

```nginx
location / {
    proxy_pass http://127.0.0.1:18080;
    proxy_set_header Host $host;
    proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
    proxy_set_header X-Forwarded-Proto $scheme;
}
```

## 本地运行（不用 Docker）

需要 Rust 1.100+（`rust-toolchain.toml` 已固定版本）。可把调试配置写进 `.env.local`，程序启动时会自动加载（优先级高于 `.env`，且已被 git 忽略）：

```bash
DOMAIN=http://localhost:18080 ADMIN_PASSWORD=your-password DATA_DIR=./data cargo run --release
```

Windows 需要 Visual Studio 2022 Build Tools 的「使用 C++ 的桌面开发」工作负载（含 MSVC 与 Windows SDK）。若在 Git Bash 里构建报 `link.exe` 相关错误，是 Git 自带的 `/usr/bin/link.exe` 抢先被找到，改用 PowerShell/CMD 构建，或把 MSVC 的 `...\VC\Tools\MSVC\<版本>\bin\Hostx64\x64` 放到 PATH 最前面即可。

## 镜像与 CI

`.github/workflows/docker.yml` 在 push `main`/`master` 或 `v*` tag 时构建 `linux/amd64` + `linux/arm64` 镜像并推到 GHCR：

```
ghcr.io/<owner>/<repo>:latest
```

NAS 更新（先在 `.env` 里指定镜像地址）：

```ini
INDE_IMAGE=ghcr.io/<owner>/<repo>:latest
```

```bash
docker compose pull && docker compose up -d
```

不填 `INDE_IMAGE` 时默认使用本地构建的 `inde-rs:local`，此时更新用
`docker compose up -d --build`，不需要访问任何镜像仓库。

> 把 `inde-rs/` 作为独立 git 仓库发布时，`.github/workflows/docker.yml` 会直接生效；若放在父仓库的子目录，请把该 workflow 移到仓库根的 `.github/workflows/`，并在 workflow 中把 `context` 设为 `inde-rs`。

## 后台使用

- 登录 `/manage`，密码来自 `ADMIN_PASSWORD`。
- **文章**：Markdown 正文；工具栏可上传图片并插入 `![](/uploads/...)`；「预览」调用服务端渲染，输出经过白名单清洗。
- **剧本 JSON**：

```json
{
  "start": "node-1",
  "nodes": [
    {"id": "node-1", "kind": "scene", "description": "场景描述",
     "choices": [{"description": "选项一", "next": "node-2"}]},
    {"id": "node-2", "kind": "end", "description": "结局描述", "choices": []}
  ]
}
```

校验规则：节点 id 唯一；`start` 存在；`scene` 至少一个选项且 `next` 存在；`end` 无选项；所有节点从 `start` 可达；至少一个可达结局。选项顺序即玩家看到的 1、2、3…

- **玩家**：可查看详情、重置某个玩家的全部进度或删除玩家。

## 安全要点

- 后台密码只来自环境变量；数据库中只存 Argon2id 哈希（仅进程内存持有）。
- 会话存 SQLite，Cookie 里是随机 token，库里存 SHA-256；`HttpOnly`、`SameSite=Lax`。
- 所有后台写操作校验 CSRF token；登录失败按 IP 限速（5 次失败锁 60 秒）。
- Markdown/HTML 经 `ammonia` 白名单清洗；上传按 magic bytes 识别图片类型，文件名随机，路径不可控。
- 响应头包含 CSP、`X-Frame-Options: DENY`、`X-Content-Type-Options: nosniff`。

## 技术栈

Rust 1.100 · Axum 0.8 · Askama 0.14 · SQLx 0.8（bundled SQLite）· Tokio · Argon2id · rust-embed。
