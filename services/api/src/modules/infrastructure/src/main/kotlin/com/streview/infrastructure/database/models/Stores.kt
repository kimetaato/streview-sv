package com.streview.infrastructure.database.models

import org.jetbrains.exposed.v1.core.Table
import org.jetbrains.exposed.v1.datetime.datetime

object StoresTable : Table() {
    val id = varchar("id", 255)
    val name = varchar("name", 255)
    val address = varchar("address", 255)
    val denwaBango = varchar("denwa_bango", 255)
    val description = text("description")
    val openingTime = text("opening_time")
    val createdAt = datetime("create_at")
    val updatedAt = datetime("updated_at")
    val deletedAt = datetime("deleted_at")
    val starCache = double("star_cache")
    val latitude = double("latitude")
    val longitude = double("longitude")
}