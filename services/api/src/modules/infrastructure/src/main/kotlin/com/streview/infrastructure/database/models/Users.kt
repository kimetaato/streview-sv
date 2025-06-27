package com.streview.infrastructure.database.models

import org.jetbrains.exposed.v1.core.Table
import org.jetbrains.exposed.v1.datetime.date

object UsersTable : Table() {
    val id = char("user_id", 28)
    val name = char("name", 50)
    val birthday = date("birthday")
    val gender = varchar("gender", 20)
    val iconUUID = varchar("icon_uuid", 255)
    val catchMode = varchar("catch_mode", 20)
}