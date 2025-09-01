package com.streview.infrastructure.database.relays

import com.streview.domain.relays.Relay
import com.streview.infrastructure.database.models.RelaysTable
import com.streview.infrastructure.database.models.RelaysTable.isReReviewed
import org.jetbrains.exposed.v1.core.ResultRow
import org.jetbrains.exposed.v1.core.statements.UpdateBuilder

fun toDomain(row: ResultRow) =
    Relay.reconstruct(
        row[RelaysTable.userID],
        row[RelaysTable.reviewUUID],
        row[RelaysTable.isRead],
        row[RelaysTable.isReReviewed]
    )

fun toTable(relay: Relay): (UpdateBuilder<*>) -> Unit {
    return {
        with(RelaysTable) {
            it[userID] = relay.userID.value
            it[reviewUUID] = relay.reviewUUID.value
            it[isRead] = relay.isRead
            it[isReReviewed] = relay.isReReviewed
        }
    }
}
