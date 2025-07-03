package com.streview.infrastructure.database.models

import org.jetbrains.exposed.v1.core.Table
import org.jetbrains.exposed.v1.datetime.date

object EncounterTable : Table() {
    val id = varchar("user_id", 255)
    val encounterId = varchar("encounter_id", 255)
    val encounterDate = date("encounter_date")
    override val primaryKey = PrimaryKey(id, encounterId, encounterDate)
}