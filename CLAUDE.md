# CLAUDE.md

このファイルは、このリポジトリで作業する際にClaude Code (claude.ai/code) にガイダンスを提供します。

## プロジェクト概要

Self-HRは、ドメイン駆動設計（DDD）アーキテクチャで構築された包括的なHR管理システムです。このシステムは、ヘキサゴナルアーキテクチャパターンとCQRSおよびイベント駆動通信を使用して、勤怠管理、契約管理、プロジェクト配置、請求書処理を扱います。

## よく使用する開発コマンド

### セットアップと環境
```bash
# 初期セットアップ（データベース、依存関係、コード生成）
make setup

# バックエンドのみセットアップ（データベース、マイグレーション、コード生成）
make setup-backend

# フロントエンドのみセットアップ（依存関係、APIクライアント生成）
make setup-frontend
```

### 開発ワークフロー
```bash
# バックエンドサーバー起動
make start-backend

# フロントエンド開発サーバー起動
make start-frontend

# UI開発用Storybook実行
make run-storybook
```

### データベース操作
```bash
# データベースコンテナ起動
make db-up

# マイグレーション実行（ローカル環境）
make db-migrate-local

# マイグレーション実行（リモート環境）
make db-migrate-remote

# jOOQデータベースコード生成
make db-codegen

# テストデータでデータベースシード
make db-seed
```

### コード生成
```bash
# GraphQLコード生成
make graphql-codegen

# OpenAPIスキーマ生成
make open-api-schema-gen

# フロントエンド用OpenAPIクライアント生成
make open-api-client-gen
```

### テストと品質
```bash
# フロントエンドテスト実行
make test-frontend

# Gradleチェック実行（テスト、lint、detektを含む）
./gradlew check

# 特定モジュールのテスト実行
./gradlew backend:domains:contract:test

# detekt静的解析実行
./gradlew detekt
```

### ビルド
```bash
# バックエンドJARビルド
make build-backend
# または
./gradlew backend:api:bootJar

# フロントエンドビルド
make build-frontend
```

## アーキテクチャと構造

### ドメイン駆動設計（DDD）アーキテクチャ

バックエンドは以下のコアドメインで厳密なDDD原則に従っています：

1. **契約ドメイン** (`backend/domains/contract/`)
   - ステータス遷移を伴う契約ライフサイクル管理
   - 請求条件の処理（時間ベース vs 固定料金）
   - 契約バージョンと期間のビジネスルール強制

2. **勤怠ドメイン** (`backend/domains/attendance/`)
   - 時間記録イベントと修正の追跡
   - ペアになった時間記録からの総時間計算
   - アクティブな契約に対する勤怠検証

3. **請求書ドメイン** (`backend/domains/invoice/`)
   - 契約請求条件に基づく請求書生成
   - 時間ベース請求のための勤怠データ統合
   - 請求書ステータス遷移管理

4. **プロジェクトドメイン** (`backend/domains/project/`)
   - プロジェクト割り当てとリソース配分処理

5. **事業主ドメイン** (`backend/domains/proprietor/`)
   - 契約者/従業員情報管理

### モジュール構造

```
backend/
├── api/              # REST/GraphQLコントローラーと設定
├── applications/     # ドメインを統括するアプリケーションサービス
├── domains/         # ドメインモジュール（DDD境界づけられたコンテキスト）
├── infrastructure/  # データベースアクセス、外部サービス（jOOQ）
├── shared/          # 共有ユーティリティとベースクラス
└── core/            # コアアプリケーション設定
```

### 主要なアーキテクチャパターン

- **ヘキサゴナルアーキテクチャ**: ドメインロジックとインフラストラクチャの明確な分離
- **CQRS**: jOOQを使用したクエリによる読み書き操作の分離
- **イベント駆動**: ドメインイベントによるクロスドメイン通信
- **値オブジェクト**: ドメインモデリングでのKotlinデータクラスの広範囲利用
- **リポジトリパターン**: インターフェースによる抽象データアクセス

### 技術スタック

**バックエンド:**
- Kotlin with Spring Boot
- GraphQL for API queries
- jOOQ for type-safe database access
- PostgreSQL 16.4
- OpenTelemetry for observability
- Detekt for static analysis
- Kotest for testing

**フロントエンド:**
- React with TypeScript
- Vite for build tooling
- TanStack Router for routing
- TanStack Query for state management
- Tailwind CSS + Radix UI components
- Storybook for component development
- Vitest for testing
- Biome for linting/formatting

## ドメインモデルとビジネスロジック

### コアエンティティ関係

1. **契約 → 勤怠**: 時間記録にはアクティブな契約が必要
2. **契約 + 勤怠 → 請求書**: 契約条件と時間記録に基づく請求計算
3. **プロジェクト ↔ 契約**: 契約にリンクしたプロジェクト割り当て

### 主要なビジネスルール

- 契約ステータス遷移は定義されたフローに従う（DRAFT → ACTIVE → TERMINATED/EXPIRED/RENEWED）
- 勤怠記録はペア（出勤/退勤）で来る必要がある
- 時間修正は監査証跡のため元イベントを参照する
- 請求計算は請求条件タイプによって異なる（時間ベース vs 固定料金）
- すべてのドメインエンティティIDでUUIDv7を一貫して使用

### 現在のアーキテクチャ課題

コードベースは、ドメイン間の循環依存を解決するためにリファクタリング中です。`docs/domain-model.md`のドキュメントで以下の計画を概説しています：
- `shared`モジュールでの共有インターフェース作成
- クロスドメインサービスでの依存性注入使用
- ドメイン間のイベント駆動通信実装

## 開発ガイドライン

### パッケージ構成
- ドメインパッケージはパターンに従う: `app.selfhr.domains.{domain}`
- 値オブジェクトは`vo/`パッケージ内
- エンティティは`entities/`パッケージ内
- 例外は`exceptions/`パッケージ内

### ID生成
- すべてのドメインはジェネレーターインターフェースを介してUUIDv7ベースのIDを使用
- パターン: `{Domain}ID`, `{Domain}IDGenerator`, `UUIDv7{Domain}IDGenerator`

### テスト戦略
- ドメインロジックの単体テスト
- データベース操作の統合テスト
- バックエンドテストでKotestを使用
- `testFixtures/`ソースセットでテストフィクスチャ利用可能

### データベースマイグレーション
- スキーマは`docker/sqldef/volume/schema.sql`で定義
- Dockerでsqldefを介してマイグレーション実行
- jOOQコード生成はスキーマ変更に従う

### フロントエンド開発
- コンポーネントは`src/components/`内で機能別に組織化
- OpenAPIスキーマから自動生成されるAPIクライアント
- コンポーネント開発用Storybookストーリー
- `src/routes/`でのルート定義