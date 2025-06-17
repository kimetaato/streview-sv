#!/bin/bash

# stopで停止していたコンテナたちを開始

source "$(dirname ${0})/init.sh"

# 開始
DOCKER_BUILDKIT=1 docker compose -p "${DOCKER_PJ_NAME}" start
