package com.streview.infrastructure.database.visits

import org.jetbrains.exposed.v1.core.Table

object VisitTable : Table("visits") {
    val userID = char("user_id", 28)
    val storeUUID = char("store_uuid", 36)
    val visitCount = integer("visit_count")
    val status = varchar("status", 20)
    override val primaryKey = PrimaryKey(userID, storeUUID)
}
