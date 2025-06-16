#!/bin/bash

# オレオレ証明書生成

# CDに移動&初期化
sh_dir="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)" # 実行場所を相対パスで取得し、そこにサブシェルで移動、pwdで取得
cd "$sh_dir" || {
    echo "Failure CD command."
    exit 1
}

# 移動
cd "../services/rev-pxy/" || {
    echo "Failed to move directory."
    exit 1
}

# 環境調整
rm -rf "ssl/"
mkdir "ssl/"
cd "ssl/" || {
    echo "Failed to move directory."
    exit 1
}

# 2048bitのRSA秘密鍵の生成
openssl genrsa 2048 >server.key
# 証明書署名要求（CSR） の生成　# -batchを使って入力をスキップ可能 # /C=国名(JP)/ST=都道府県(Osaka)/L=市町村名(Osaka-shi)/O=組織名(\"Example Inc\")/OU=組織単位(Foo)/CN=コモンネーム(hoge.com)
openssl req -new -key server.key -subj "/C=JP" >server.csr
# CRSを元にハッシュ方式SHA-512で期限10年のサーバー証明書の生成 x509サブコマンド:証明書関連処理
openssl x509 -req -sha512 -days 3650 -signkey server.key <server.csr >server.crt
# 内容確認
openssl x509 -text -noout -in server.crt
