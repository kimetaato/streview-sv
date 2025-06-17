#!/bin/bash

# developのみ破壊

source "$(dirname ${0})/init.sh"

# develop削除 既存のコンテナの停止とそのイメージの削除 --remove-orphans: Compose ファイルで定義していないサービス用のコンテナも削除
docker compose -p "${DOCKER_PJ_NAME}" down --rmi all --remove-orphans --volumes --timeout 15
