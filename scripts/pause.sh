#!/bin/bash

# サービス(コンテナ)たちを停止

source "$(dirname ${0})/init.sh"

# develop停止
DOCKER_BUILDKIT=1 docker compose -p "${DOCKER_PJ_NAME}" stop
