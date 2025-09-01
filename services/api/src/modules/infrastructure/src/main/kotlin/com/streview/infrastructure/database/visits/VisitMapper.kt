package com.streview.infrastructure.database.visits

import com.streview.domain.visits.Visit
import org.jetbrains.exposed.v1.core.ResultRow
import org.jetbrains.exposed.v1.core.statements.UpdateBuilder

fun toDomain(row: ResultRow) =
    Visit.reconstruct(
        userID = row[VisitTable.userID],
        storeUUID = row[VisitTable.storeUUID],
        visitCount = row[VisitTable.visitCount],
        status = row[VisitTable.status]
    )

fun toTable(visit: Visit): (UpdateBuilder<*>) -> Unit {
    return {
        with(VisitTable) {
            it[userID] = visit.userID.value
            it[storeUUID] = visit.storeUUID.value
            it[visitCount] = visit.visitCount
            it[status] = visit.status.value
        }
    }
}
