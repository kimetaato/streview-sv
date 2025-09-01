package com.streview.configure

import com.streview.infrastructure.database.encounters.EncounterTable
import com.streview.infrastructure.database.images.ImagesTable
import com.streview.infrastructure.database.reviews.ReviewTable
import com.streview.infrastructure.database.stores.StoreTable
import com.streview.infrastructure.database.users.UserTable
import com.streview.infrastructure.database.visits.VisitTable
import io.r2dbc.spi.ConnectionFactories
import io.r2dbc.spi.ConnectionFactory
import io.r2dbc.spi.ConnectionFactoryOptions.*
import kotlinx.coroutines.runBlocking
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
            SchemaUtils.create(
                ImagesTable,
                UserTable,
                StoreTable,
                EncounterTable,
                ReviewTable,
                VisitTable
            )

            if (StoreTable.selectAll().empty()) {
                StoreTable.insert {
                    it[storeUUID] = "85e15cf9-3555-45e4-ad77-920432ad937d"
                    it[name] = "木製ロケット"
                    it[genre] = "オムライス"
                    it[address] = "〒610-0121 京都府城陽市寺田正道9−14"
                    it[phoneNumber] = "0774-26-8440"
                    it[description] = "うまいオムライス屋さん"
                    it[openingTime] = "日曜 11:00~18:00" +
                        "月曜 11:00~16:00" +
                        "火曜 定休日" +
                        "水曜 11:00~21:00" +
                        "木曜 11:00~21:00" +
                        "金曜 11:00~22:00" +
                        "土曜 11:00~17:00"
                    it[latitude] = 34.85743178543728
                    it[longitude] = 135.78065442190467
                }
            }
        }
    }
}
