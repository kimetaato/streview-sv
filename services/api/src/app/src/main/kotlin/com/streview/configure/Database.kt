package com.streview.configure

import com.streview.infrastructure.database.models.UsersTable
import io.ktor.server.application.*
import io.r2dbc.spi.ConnectionFactories
import io.r2dbc.spi.ConnectionFactory
import io.r2dbc.spi.ConnectionFactoryOptions.*
import kotlinx.coroutines.runBlocking
import org.jetbrains.exposed.v1.core.vendors.PostgreSQLDialect
import org.jetbrains.exposed.v1.r2dbc.*
import org.jetbrains.exposed.v1.r2dbc.transactions.suspendTransaction


fun Application.configureDatabase() {
    val connectionFactory: ConnectionFactory = ConnectionFactories.get(
        builder()
            .option(DRIVER, "pool")
            .option(PROTOCOL, "postgresql")
            .option(HOST, "streview_db")
            .option(PORT, 5432)
            .option(USER, "root")
            .option(PASSWORD, "root-pass")
            .option(DATABASE, "streview")
            .build()
    )

    R2dbcDatabase.connect(
        connectionFactory,
        databaseConfig = R2dbcDatabaseConfig {
            explicitDialect = PostgreSQLDialect()
        }
    )

    runBlocking {
        suspendTransaction {
            SchemaUtils.create(UsersTable)
            if (UsersTable.selectAll().empty()) {
                UsersTable.insert {
                    it[name] = "Streview"
                    it[email] = "streview@streview.com"
                }
            }
        }
    }
}