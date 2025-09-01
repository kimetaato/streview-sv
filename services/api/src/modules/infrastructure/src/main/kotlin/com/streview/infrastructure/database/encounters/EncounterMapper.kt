package com.streview.infrastructure.database.encounters

import com.streview.domain.commons.UserID
import com.streview.domain.encounters.Encounter
import org.jetbrains.exposed.v1.core.ResultRow
import org.jetbrains.exposed.v1.core.statements.UpdateBuilder

fun toDomain(list: List<ResultRow>): Encounter =
    Encounter.reconstruct(
        actorID = list.first()[EncounterTable.userID],
        encounterDate = list.first()[EncounterTable.encounterDate],
        encounterIDs = list.map { item -> item[EncounterTable.encounterId] }
    )

fun toTable(encounter: Encounter, encounterID: UserID): (UpdateBuilder<*>) -> Unit {
    with(EncounterTable) {
        return {
            it[userID] = encounter.actorID.value
            it[encounterId] = encounterID.value
            it[encounterDate] = encounter.encounterDate.value
        }
    }
}
