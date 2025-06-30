package com.streview.infrastructure.database.models

import org.jetbrains.exposed.v1.core.Table
import org.jetbrains.exposed.v1.datetime.datetime

object EncounterTable : Table() {
    val id = varchar("user_id", 255)
    val encounterId = varchar("encounter_id", 255)
    val localDate = datetime("local_date")
    override val primaryKey = PrimaryKey(id, encounterId, localDate)
}