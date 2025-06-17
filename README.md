# streview-sv

すとれびゅ！のdockerプロジェクト

## 開発環境構築

```bash
# デバッグ用のオレオレ認証のための鍵作成
bash ./scripts/key-gen.sh

# コンテナビルド&実行
bash ./scripts/rebuild.sh
```

## デバック

### Postgresコンテナでの操作

```bash
# ログイン
psql -U root -d streview

# DB一覧
\l

# DB選択
\c streview

# テーブル一覧
\dt

# 終了
\q
```
