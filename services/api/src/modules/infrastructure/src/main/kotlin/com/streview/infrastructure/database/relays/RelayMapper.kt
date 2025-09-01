package com.streview.infrastructure.database.relays

import com.streview.domain.relays.Relay
import com.streview.infrastructure.database.models.RelaysTable
import org.jetbrains.exposed.v1.core.ResultRow
import org.jetbrains.exposed.v1.core.statements.UpdateBuilder

fun toDomain(row: ResultRow) =
    Relay.reconstruct(
        userID = row[RelaysTable.userID],
        reviewUUID = row[RelaysTable.reviewUUID],
        isReReview = row[RelaysTable.isReReview],
        isRead = row[RelaysTable.isRead],
    )

fun toTable(relay: Relay): (UpdateBuilder<*>) -> Unit {
    return {
        with(RelaysTable) {
            it[userID] = relay.userID.value
            it[reviewUUID] = relay.reviewUUID.value
            it[isReReview] = relay.isReReview
            it[isRead] = relay.isRead
        }
    }
}
