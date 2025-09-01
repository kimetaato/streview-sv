package com.streview.infrastructure.database.relays

import org.jetbrains.exposed.v1.core.Table

object RelayTable : Table("relays") {
    val userID = char("user_id", 28)
    val reviewUUID = char("review_uuid", 36)
    val isReReview = bool("is_re_review")
    val isRead = bool("is_read")
    override val primaryKey = PrimaryKey(userID, reviewUUID)
}
