package com.streview.infrastructure.database.stores

import org.jetbrains.exposed.v1.core.Table

object StoreTable : Table("stores") {
    val storeUUID = varchar("id", 255)
    val name = varchar("name", 255)
    val genre = varchar("genre", 255)
    val address = varchar("address", 255)
    val phoneNumber = varchar("phone_number", 255)
    val description = text("description")
    val openingTime = text("opening_time")
    val latitude = double("latitude")
    val longitude = double("longitude")
    override val primaryKey = PrimaryKey(storeUUID)
}
