package com.streview.infrastructure.database.encounters

import com.streview.domain.commons.UserID
import com.streview.domain.encounters.Encounter
import com.streview.domain.encounters.EncounterDate
import com.streview.infrastructure.database.models.EncounterTable
import org.jetbrains.exposed.v1.core.ResultRow


fun toDomain(list: List<ResultRow>): Encounter {
    val encounterIDs = mutableListOf<UserID>()

    val actorID = UserID(list.first()[EncounterTable.id])
    val encounterDate = EncounterDate(list.first()[EncounterTable.encounterDate])

    list.forEach { item ->
        val encounterId = item[EncounterTable.encounterId]
        encounterIDs.add(UserID(encounterId))
    }

    return Encounter.factory(
        actorID,
        encounterDate,
        encounterIDs.toList()
    )
}

//fun EncounterRepository.toTable(encounter: Encounter): List<(UpdateBuilder<*>) -> Unit> {
//    return encounter.encounterIDs.map { encounterID ->
//        {
//            it[EncounterTable.id] = encounter.actorID.value
//            it[EncounterTable.encounterDate] = encounter.encounterDate.value
//            it[EncounterTable.encounterId] = encounterID.value
//        }
//    }
//}
