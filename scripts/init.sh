#!/bin/bash

# スクリプトのカレントディレクトリに移動する
# `source <実行場所からのこのファイルの場所> <移動した場所からの読み込む.evnファイルの場所>`

# htu
# 呼び出しファイルと同じ場所に置き、`source "$(dirname ${0})/init.sh"`と呼び出すと、実行場所がどこでもファイルからの相対パスで動かせる
# 読み込む.envファイルのパスを渡すこと

# CDに移動&初期化
sh_dir="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)" # 実行場所を相対パスで取得し、そこにサブシェルで移動、pwdで取得
cd "$sh_dir" || {
  echo "Failure CD command."
  exit 1
}
# source ${1:-./.env}

SCR_BS_DIR="$(pwd)" # script base dir
