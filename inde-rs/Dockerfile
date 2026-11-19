FROM rust:1.100.0-bookworm AS build
# 直接使用镜像内置的 1.100.0 工具链，避免 rustup 联网解析 rust-toolchain.toml
#（国内网络下 static.rust-lang.org 常握手失败）。
ENV RUSTUP_TOOLCHAIN=1.100.0
WORKDIR /src
COPY . .
RUN cargo build --release --locked

FROM debian:bookworm-slim AS runtime
RUN apt-get update \
    && apt-get install -y --no-install-recommends ca-certificates curl \
    && rm -rf /var/lib/apt/lists/*

RUN useradd --uid 10001 --home-dir /data --shell /usr/sbin/nologin --no-create-home inde \
    && mkdir -p /data/uploads \
    && chown -R 10001:10001 /data

COPY --from=build /src/target/release/inde-rs /usr/local/bin/inde-rs
RUN chmod 0755 /usr/local/bin/inde-rs

ENV DATA_DIR=/data \
    PORT=8080 \
    RUST_LOG=info

USER 10001:10001
WORKDIR /data
VOLUME ["/data"]
EXPOSE 8080

HEALTHCHECK --interval=30s --timeout=3s --start-period=15s --retries=3 \
    CMD curl -fsS "http://127.0.0.1:${PORT:-8080}/healthz" || exit 1

ENTRYPOINT ["/usr/local/bin/inde-rs"]
