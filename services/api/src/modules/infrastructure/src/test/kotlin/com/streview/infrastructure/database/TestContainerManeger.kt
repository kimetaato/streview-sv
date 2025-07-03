package com.streview.infrastructure.database

import com.streview.infrastructure.database.models.EncounterTable
import io.kotest.core.annotation.AutoScan
import io.kotest.core.listeners.AfterProjectListener
import io.kotest.core.listeners.BeforeProjectListener
import io.kotest.extensions.testcontainers.ContainerExtension
import io.r2dbc.spi.ConnectionFactories
import io.r2dbc.spi.ConnectionFactory
import io.r2dbc.spi.ConnectionFactoryOptions.*
import org.jetbrains.exposed.v1.core.vendors.PostgreSQLDialect
import org.jetbrains.exposed.v1.r2dbc.R2dbcDatabase
import org.jetbrains.exposed.v1.r2dbc.R2dbcDatabaseConfig
import org.jetbrains.exposed.v1.r2dbc.SchemaUtils
import org.jetbrains.exposed.v1.r2dbc.transactions.suspendTransaction
import org.testcontainers.containers.GenericContainer

/**
 * テストコンテナとKotest拡張機能のインスタンスをシングルトンとして管理するオブジェクト。
 * これにより、モジュール内の全テストで同じコンテナインスタンスを共有できる。
 */
object TestDatabaseManager {
    //  PostgreSQLコンテナを定義
    val postgresContainer = GenericContainer("postgres:15.13-bookworm").apply {
        withExposedPorts(5432)
        withEnv("POSTGRES_USER", "test")
        withEnv("POSTGRES_PASSWORD", "test")
        withEnv("POSTGRES_DB", "test")
    }

    // ContainerExtensionもシングルトンとして保持する
    val dbExtension = ContainerExtension(postgresContainer)
}

@AutoScan
object DatabaseSetupListener : BeforeProjectListener, AfterProjectListener {
    override suspend fun beforeProject() {
        // コンテナを起動させるために、一度だけダミーでインストールする
        // これにより、TestDatabaseManager.postgresContainer が起動する
        TestDatabaseManager.dbExtension.mount { }

        // データベース接続とスキーマ作成を一度だけ実行
        val postgres = TestDatabaseManager.postgresContainer
        // 起動したコンテナの情報を元に接続情報を設定
        val connectionFactory: ConnectionFactory = ConnectionFactories.get(
            builder()
                .option(DRIVER, "pool")
                .option(PROTOCOL, "postgresql")
                .option(HOST, postgres.host)
                .option(PORT, postgres.firstMappedPort)
                .option(USER, "test")
                .option(PASSWORD, "test")
                .option(DATABASE, "test")
                .build()
        )


        // コネクションプールの作成
        R2dbcDatabase.connect(
            connectionFactory,
            databaseConfig = R2dbcDatabaseConfig {
                explicitDialect = PostgreSQLDialect()
            }
        )

        suspendTransaction {
            SchemaUtils.create(
                EncounterTable,
            )
        }
    }

    override suspend fun afterProject() {
        // 全テスト終了後にコンテナを停止させる
        TestDatabaseManager.postgresContainer.stop()
    }
}
