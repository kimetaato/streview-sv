package com.streview.infrastructure.database.models

import org.jetbrains.exposed.v1.core.Table

object UsersTable : Table() {
    val id = integer("id").autoIncrement()
    val name = varchar("name", 255)
    val email = varchar("email", 255)

}

