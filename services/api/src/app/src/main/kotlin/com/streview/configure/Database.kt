package com.streview.configure

import com.streview.infrastructure.database.models.StoresTable
import com.streview.infrastructure.database.models.UsersTable
import io.r2dbc.spi.ConnectionFactories
import io.r2dbc.spi.ConnectionFactory
import io.r2dbc.spi.ConnectionFactoryOptions.*
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.*
import org.jetbrains.exposed.v1.core.vendors.PostgreSQLDialect
import org.jetbrains.exposed.v1.r2dbc.*
import org.jetbrains.exposed.v1.r2dbc.transactions.suspendTransaction


fun configureDatabase() {
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
            SchemaUtils.create(UsersTable, StoresTable)

            if (StoresTable.selectAll().empty()) {
                StoresTable.insert {
                    it[id] = "85e15cf9-3555-45e4-ad77-920432ad937d"
                    it[name] = "木製ロケット"
                    it[address] = "〒610-0121 京都府城陽市寺田正道9−14"
                    it[denwaBango] = "0774-26-8440"
                    it[description] = "うまいオムライス屋さん"
                    it[openingTime] = "日曜 11:00~18:00" +
                            "月曜 11:00~16:00" +
                            "火曜 定休日" +
                            "水曜 11:00~21:00" +
                            "木曜 11:00~21:00" +
                            "金曜 11:00~22:00" +
                            "土曜 11:00~17:00"
                    val currentMoment: Instant = Clock.System.now()
                    val datetimeInUtc: LocalDateTime = currentMoment.toLocalDateTime(TimeZone.UTC)
                    it[createdAt] = datetimeInUtc
                    it[updatedAt] = datetimeInUtc
                    it[deletedAt] = datetimeInUtc
                    it[starCache] = 4.9
                    it[latitude] = 34.85743178543728
                    it[longitude] = 135.78065442190467
                }
            }
        }
    }
}