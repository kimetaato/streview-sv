package com.streview.infrastructure.database.models

import org.jetbrains.exposed.v1.core.Table
import org.jetbrains.exposed.v1.datetime.datetime

object ReviewTable : Table("reviews") {
    val reviewUUID = char("review_uuid", 36)
    val comment = text("comment")
    val star = decimal("star", 2, 1)
    val writerId = char("writer_id", 28)
    val storeUUID = char("store_uuid", 36)
    val isPublic = bool("is_public")
    val imageUUIDs = array<String>("image_uuids", 10)
    val createdAt = datetime("created_at")
    val updatedAt = datetime("updated_at")
    override val primaryKey = PrimaryKey(reviewUUID)
}
