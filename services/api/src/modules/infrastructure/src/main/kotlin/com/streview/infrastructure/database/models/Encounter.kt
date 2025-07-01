package com.streview.infrastructure.database.models

import org.jetbrains.exposed.v1.core.Table
import org.jetbrains.exposed.v1.datetime.date

object EncounterTable : Table() {
    val id = varchar("user_id", 255)
    val encounterId = varchar("encounter_id", 255)
    val localDate = date("local_date")
    override val primaryKey = PrimaryKey(id, encounterId, localDate)
}