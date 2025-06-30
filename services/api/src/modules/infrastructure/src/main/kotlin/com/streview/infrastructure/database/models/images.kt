package com.streview.infrastructure.database.models

import org.jetbrains.exposed.v1.core.Table

object ImagesTable : Table() {
    val id = varchar("image_uuid", 40)
    val fileName = varchar("file_name", 50).uniqueIndex()
    override val primaryKey = PrimaryKey(id)
}