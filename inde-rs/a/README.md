# ky-oooc-ink — 纯静态首页

`a/` 是一个完全独立的纯静态站点：**一个 `index.html` + 一个 MP3 + nginx 镜像**，不依赖根目录的 Rust 服务。

- 单文件页面（内联 CSS/JS，零外部依赖）：实时时钟、四个入口磁贴（点击提示「建设中」）、背景音乐播放器（循环播放）
- 自适应深浅色与移动端
- 镜像名 `ky-oooc-ink:local`，容器名 `ky-oooc-ink`，默认端口 **18080**

## 目录结构

```
a/
├── index.html          # 页面本体
├── audio/a.mp3         # 背景音乐
├── default.conf        # 容器内 nginx 站点配置（首页/缓存/安全头）
├── Dockerfile          # nginx:1.27-alpine
├── docker-compose.yml  # 编排配置
└── README.md
```

## 部署（Docker Compose，推荐）

```bash
cd a
docker compose up -d --build
```

访问 `http://<服务器IP>:18080`。

端口可通过环境变量覆盖：

```bash
A_PORT=9000 docker compose up -d
```

镜像名可通过 `A_IMAGE` 覆盖，例如 `A_IMAGE=ghcr.io/you/ky-oooc-ink:latest docker compose up -d`。

## 部署（docker run）

```bash
cd a
docker build -t ky-oooc-ink:local .
docker run -d \
  --name ky-oooc-ink \
  --restart unless-stopped \
  -p 18080:80 \
  --read-only \
  --tmpfs /var/cache/nginx \
  --tmpfs /var/run \
  --tmpfs /tmp \
  --cap-drop ALL \
  --cap-add CHOWN --cap-add SETGID --cap-add SETUID --cap-add NET_BIND_SERVICE \
  --security-opt no-new-privileges:true \
  ky-oooc-ink:local
```

> nginx 官方镜像在只读 rootfs 下运行需要上面三个 tmpfs 挂载；图省事可去掉 `--read-only` 及相关 `--tmpfs`/`--cap-add`。

健康检查已内置在镜像里（`wget http://127.0.0.1/index.html`），`docker ps` 可见 healthy 状态。

## 更新

修改 `index.html` / 替换 `audio/a.mp3` 后重新构建即可，数据无状态、可随时重建：

```bash
cd a
docker compose up -d --build
```

## 排障：部署后 403

直接访问容器端口也是 403（nginx 默认 403 页）时，说明 nginx 起来了但拿不到首页，先在 NAS 上确认：

```bash
docker ps --filter name=ky-oooc-ink
docker logs --tail 50 ky-oooc-ink            # 看下面两类错误
docker exec ky-oooc-ink ls -la /usr/share/nginx/html
docker exec ky-oooc-ink stat -c '%a %U:%G %n' /usr/share/nginx/html/index.html
```

按日志区分原因：

| nginx 日志 | 原因 | 处理 |
|---|---|---|
| `directory index of "/usr/share/nginx/html/" is forbidden` | index.html 没进镜像（构建上下文不对 / 用了旧镜像） | 在 `a/` 下 `docker compose build --no-cache` 重建 |
| `open() "/usr/share/nginx/html/index.html" failed (13: Permission denied)` | 文件权限问题：SMB/Windows 拷贝后变成 600，nginx worker（uid 101）读不到 | 已在 Dockerfile 构建期 `chmod -R a+rX`；`docker compose build --no-cache && docker compose up -d --force-recreate` |
| 以上都没有、直接 curl 容器端口却正常 | 403 来自外层反向代理（NPM 的 Access List / 目标端口不对 / 走了错误的 vhost） | 检查 NPM 转发目标是否为 `NAS_IP:18080`，并确认没有启用 Access List |



若前有 Nginx/Caddy 统一入口，转发到本容器：

```nginx
server {
    listen 443 ssl http2;
    server_name oooc.ink;

    # ssl_certificate ...; ssl_certificate_key ...;

    location / {
        proxy_pass http://127.0.0.1:18080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }
}
```

证书可用 certbot 签发，方式与其他站点一致。

## 自定义

| 想改什么 | 改哪里 |
|---|---|
| 四个入口的名称/链接 | `index.html` 中 `.links` 里的 `<a href="..." data-name="...">` |
| 「建设中」提示文案 | `index.html` 脚本里 `' · 建设中'` |
| 背景音乐 | 替换 `a/audio/a.mp3`，或改 `<audio src="...">` |
| 标题/署名/页脚 | `index.html` 顶部的 `<title>`、`.subtitle`、`<footer>` |
| 端口 | `docker-compose.yml` 的 `A_PORT`，或 `docker run -p` |
