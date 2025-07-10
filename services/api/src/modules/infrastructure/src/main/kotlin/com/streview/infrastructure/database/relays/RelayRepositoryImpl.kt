package com.streview.infrastructure.database.relays

import com.streview.domain.relays.Relay
import com.streview.domain.relays.RelaysRepository
import com.streview.infrastructure.database.models.RelaysTable
import kotlinx.coroutines.flow.singleOrNull
import org.jetbrains.exposed.v1.core.SqlExpressionBuilder.eq
import org.jetbrains.exposed.v1.core.and
import org.jetbrains.exposed.v1.r2dbc.insert
import org.jetbrains.exposed.v1.r2dbc.select

class RelayRepositoryImpl : RelaysRepository {
    override suspend fun findByUserIdAndReviewUUID(
        userID: String,
        reviewUUID: String
    ): Relay? {
        return RelaysTable
            .select(
                RelaysTable.userID,
                RelaysTable.reviewUUID,
                RelaysTable.isReReviewed
            )
            .where((RelaysTable.userID eq userID) and (RelaysTable.reviewUUID eq reviewUUID))
            .singleOrNull()?.let { row ->
                toDomain(row)
            }
    }

    override suspend fun save(relay: Relay) {
        RelaysTable.insert { statement ->
            toTable(relay)(statement)
        }
    }
}