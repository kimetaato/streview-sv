# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Docker開発環境

このプロジェクトは全てDocker環境で開発されます。すべてのGradleコマンドは`streview_api`コンテナ内で実行する必要があります。

### 必要な開発コマンド

```bash
# ビルド
docker exec streview_api bash -c "cd /app/src && ./gradlew build"

# テスト実行
docker exec streview_api bash -c "cd /app/src && ./gradlew test"

# コード品質チェック（Detekt）
docker exec streview_api bash -c "cd /app/src && ./gradlew detektAll"

# アプリケーション起動
docker exec streview_api bash -c "cd /app/src && ./gradlew run"

# サンプルデータ挿入
docker exec streview_api bash -c "cd /app/src && ./gradlew addSampleData"

# 特定のテスト実行
docker exec streview_api bash -c "cd /app/src && ./gradlew test --tests 'TestClassName'"
```

### 開発環境構築

1. .envファイルを作成（.env.templateを参考）
2. デバッグ用認証鍵の生成: `bash ./scripts/gen_key.sh`
3. コンテナビルド&実行: `bash ./scripts/rebuild.sh`
4. Firebase秘密鍵を`./app/src/main/resources/admin.json`に配置

## アーキテクチャ

### クリーンアーキテクチャ構成

- **app**: アプリケーションエントリポイント、設定、DI
- **modules/presentation**: Ktorコントローラー、ルーティング
- **modules/application**: ユースケース、アプリケーションサービス
- **modules/domain**: ドメインエンティティ、リポジトリインターface、ドメインサービス
- **modules/infrastructure**: データベース実装、外部API、ストレージ

### 技術スタック

- **Web Framework**: Ktor 3.1.3
- **ORM**: Exposed with R2DBC（非同期）
- **Database**: PostgreSQL
- **DI**: Koin
- **Testing**: Kotest + MockK + Testcontainers
- **Code Quality**: Detekt
- **Authentication**: Firebase Auth

### 主要な設計パターン

- **UseCase Pattern**: `UseCase<InputPort, OutputPort>` インターフェースを使用
- **Repository Pattern**: ドメイン層でインターface、インフラ層で実装
- **Result Pattern**: `com.github.michaelbull.kotlin-result` でエラーハンドリング
- **Value Object**: ドメインの値オブジェクト（例: `Name`, `Address`, `Tel`）
- **Event Bus**: ドメインイベントの発行と購読

### 開発規約

- パッケージ構造: `com.streview.{layer}.{feature}`
- ファイル配置: 機能ごとにパッケージを分ける
- テストクラス命名: `{対象クラス名}Test`
- エンティティは`companion object`で`create`（新規作成）と`reconstruct`（復元）を提供
- バリデーションエラーは`ValidationError`、ビジネスエラーは`BusinessException`

## Docker Compose構成

- **streview_api**: Ktorアプリケーションサーバー（ポート: 8080）
- **streview_db**: PostgreSQLデータベース
- **streview_rev-pxy**: Nginxリバースプロキシ（ポート: 80, 443）
- **streview_mock**: Prism APIモック（ポート: 4010）

## 外部API連携

- **Google Places API**: 店舗情報取得
- **Hot Pepper API**: グルメ情報取得
- API키は環境変数で管理（GOOGLE_API_KEY, HOT_PEPPER_API_KEY）