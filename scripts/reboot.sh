#!/bin/bash

# コンテナの再起動を行うが、イメージの再構築や更新された設定は反映させない

source "$(dirname ${0})/init.sh"

# 再起動(stop + start) restartでは-fオプションのヘルスチェックと順序を守ってくれなかった
bash ./pause.sh
bash ./resume.sh
