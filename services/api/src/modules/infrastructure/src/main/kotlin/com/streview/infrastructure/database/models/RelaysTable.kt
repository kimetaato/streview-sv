package com.streview.infrastructure.database.models

import org.jetbrains.exposed.v1.core.Table

object RelaysTable : Table() {
    val userID = char("user_id", 28)
    val reviewUUID = char("review_uuid", 36)
    val isReReviewed = bool("is_re_reviewed")
    override val primaryKey = PrimaryKey(userID, reviewUUID)
}
