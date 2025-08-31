# API テストガイド

このディレクトリには、streview API をテストするための IntelliJ IDEA HTTP Client ファイルが含まれています。

## 必要な環境

### IntelliJ IDEA
- HTTP Client (標準搭載) - 追加インストール不要
- IntelliJ IDEA 2018.1 以降で利用可能

## セットアップ

1. **アプリケーションの起動**
   ```bash
   docker exec streview_api bash -c "cd /app/src && ./gradlew run"
   ```

2. **Firebase認証設定**
   - `api-tests.http` ファイル内の以下の変数を実際の値に置き換え：
     - `@firebaseApiKey`: Firebase Console の「プロジェクトの設定」→「全般」→「ウェブAPIキー」
     - `@email`: テスト用のメールアドレス
     - `@password`: テスト用のパスワード（6文字以上）

## 📋 テスト実行手順

### ステップ1: Firebase認証
1. **新規ユーザー登録**（初回のみ）
   - "ステップ1: 新規ユーザー登録" を実行
   - 成功すると自動的にトークンが設定される

2. **サインイン**（既存ユーザーまたは登録後）
   - "ステップ2: サインイン" を実行
   - 成功すると自動的にトークンが設定される

3. **トークン確認**（必要に応じて）
   - "🔍 トークン確認用" を実行してトークンの有効性を確認

### ステップ2: APIテスト実行
Firebase認証が完了したら、以下のAPIテストを実行可能：
- ユーザー登録
- レビュー投稿
- マイレビュー一覧取得

## テストファイル

### `api-tests.http`
メインのテストファイル。以下のエンドポイントをテスト可能：

- **基本確認**
  - `GET /` - サーバー生存確認
  - `GET /swagger` - API ドキュメント

- **ユーザー関連**
  - `POST /users/registers` - ユーザー登録（マルチパート）
  - `GET /users/profiles` - プロフィール取得（未実装）
  - `PUT /users/profiles` - プロフィール更新（未実装）

- **レビュー関連**
  - `POST /reviews/` - レビュー投稿（マルチパート）
  - `GET /reviews/post` - マイレビュー一覧

## 使用方法

1. **IntelliJ IDEA での実行**
   - `api-tests.http` を開く
   - 各リクエストの左側の緑色の実行ボタン（▶️）をクリック
   - または、リクエスト内にカーソルを置いて `Ctrl+Enter` (Windows/Linux) / `Cmd+Enter` (Mac)
   - 下部の「Services」タブまたは「HTTP Client」タブで結果を確認

2. **変数の設定**
   - ファイル上部の変数（@baseUrl, @firebaseApiKey等）を実際の値に置き換え
   - Firebase認証が成功すると、トークンが自動的にfirebaseToken変数に保存される

## テスト例

### 認証なしテスト（401エラーを期待）
```http
POST http://localhost:8080/users/registers
Content-Type: multipart/form-data

# レスポンス: 401 Unauthorized
```

### 認証ありテスト（Firebase JWT必要）
```http
POST http://localhost:8080/users/registers
Authorization: Bearer your-firebase-jwt-token
Content-Type: multipart/form-data

# レスポンス: 201 Created (成功時)
```

## マルチパートデータのテスト

ユーザー登録とレビュー投稿では、以下のフォームデータが必要：

### ユーザー登録
- `name`: ユーザー名
- `birthday`: 生年月日 (YYYY-MM-DD)
- `gender`: 性別
- `file`: プロフィール画像

### レビュー投稿
- `storeUUID`: 店舗ID
- `comment`: レビューコメント
- `star`: 評価 (数値)
- `file`: レビュー画像

## トラブルシューティング

### 🚫 Firebase認証エラー
**ERROR_EMAIL_NOT_FOUND**
- 原因: 指定したメールアドレスのアカウントが存在しない
- 解決策: "ステップ1: 新規ユーザー登録" を先に実行

**ERROR_INVALID_PASSWORD**
- 原因: パスワードが間違っている
- 解決策: 正しいパスワードを設定するか、新規登録で新しいアカウントを作成

**ERROR_WEAK_PASSWORD**
- 原因: パスワードが6文字未満
- 解決策: 6文字以上のパスワードを設定

### 🚫 APIエラー  
**401 Unauthorized**
- 原因: Firebase認証トークンが無効または未設定
- 解決策: Firebase認証セクションを実行してトークンを取得

**403 Forbidden**
- 原因: 権限不足
- 解決策: 正しいFirebaseプロジェクトとAPIキーを使用していることを確認

### 🚫 サーバーエラー
**接続エラー**
1. アプリケーションが起動していることを確認: `curl http://localhost:8080/`
2. Dockerコンテナが実行中か確認: `docker ps`
3. アプリケーション再起動: `docker exec streview_api bash -c "cd /app/src && ./gradlew run"`

### 🚫 ファイルアップロードエラー
1. `test-files/` ディレクトリに画像ファイルが存在することを確認
2. ファイルパスが正しいことを確認
3. マルチパートフォームデータの境界が正しいことを確認

## 💡 便利なヒント

### トークンの確認方法
- "🔍 トークン確認用" リクエストで現在のトークン状態をチェック
- コンソール出力で詳細な情報を確認

### 自動トークン管理
- Firebase認証が成功すると、トークンは自動的に変数に保存される
- 手動でトークンを設定する必要はない

### デバッグ情報
- 各リクエストの実行後、IntelliJ IDEAの「Services」タブでコンソール出力を確認
- エラーメッセージと成功メッセージが表示される

## 期待される結果

- **認証なしリクエスト**: `401 Unauthorized`
- **正しい認証付きリクエスト**: `201 Created` または `200 OK`
- **不正なデータ**: `400 Bad Request` または適切なエラーメッセージ