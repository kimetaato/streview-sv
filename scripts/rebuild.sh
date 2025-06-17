#!/bin/bash

# 最初の起動や更新の反映に使う
# 既存のイメージやキャッシュを破棄してビルド&起動し直す

source "$(dirname ${0})/init.sh"

# 破棄処理
docker compose -p "${DOCKER_PJ_NAME}" down --rmi all --remove-orphans --volumes --timeout 15

# キャッシュなしでビルド
DOCKER_BUILDKIT=1 docker compose -p "${DOCKER_PJ_NAME}" -f ../compose.yml build --no-cache

# 起動
DOCKER_BUILDKIT=1 docker compose -p "${DOCKER_PJ_NAME}" -f ../compose.yml up -d
