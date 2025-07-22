package com.streview.infrastructure.database.encounters

import com.streview.domain.encounters.Encounter
import com.streview.infrastructure.database.models.EncounterTable
import org.jetbrains.exposed.v1.core.ResultRow

fun toDomain(list: List<ResultRow>): Encounter {
    val actorID = list.first()[EncounterTable.id]
    val encounterDate = list.first()[EncounterTable.encounterDate]

    val encounterIDs = mutableListOf<String>()
    list.forEach { item ->
        encounterIDs.add(item[EncounterTable.encounterId])
    }

    return Encounter.factory(
        actorID,
        encounterDate,
        encounterIDs.toList()
    )
}
