FROM nginx:1.27-alpine

COPY default.conf /etc/nginx/conf.d/default.conf
COPY index.html /usr/share/nginx/html/index.html
COPY audio /usr/share/nginx/html/audio

# 源文件经 SMB/Windows 拷贝到 NAS 后模式可能变成 600（root 所有），
# nginx worker（uid 101 nginx）读不到会直接返回 403。
# 构建期统一放开读权限，并校验首页确实进了镜像，问题在这里暴露而不是线上。
RUN test -f /usr/share/nginx/html/index.html \
    && chmod -R a+rX /usr/share/nginx/html

HEALTHCHECK --interval=30s --timeout=3s --start-period=5s --retries=3 \
    CMD wget -qO- http://127.0.0.1/index.html >/dev/null || exit 1

EXPOSE 80
