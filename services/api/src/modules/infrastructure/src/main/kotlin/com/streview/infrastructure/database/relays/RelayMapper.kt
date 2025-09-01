package com.streview.infrastructure.database.relays

import com.streview.domain.relays.Relay
import org.jetbrains.exposed.v1.core.ResultRow
import org.jetbrains.exposed.v1.core.statements.UpdateBuilder

fun toDomain(row: ResultRow) =
    Relay.reconstruct(
        userID = row[RelayTable.userID],
        reviewUUID = row[RelayTable.reviewUUID],
        isReReview = row[RelayTable.isReReview],
        isRead = row[RelayTable.isRead],
    )

fun toTable(relay: Relay): (UpdateBuilder<*>) -> Unit {
    return {
        with(RelayTable) {
            it[userID] = relay.userID.value
            it[reviewUUID] = relay.reviewUUID.value
            it[isReReview] = relay.isReReview
            it[isRead] = relay.isRead
        }
    }
}
