package com.streview.infrastructure.database.encounters

import com.streview.domain.commons.UserID
import com.streview.domain.encounters.Encounter
import com.streview.domain.encounters.EncounterDate
import com.streview.domain.encounters.EncounterRepository
import com.streview.infrastructure.database.models.EncounterTable
import kotlinx.coroutines.flow.toList
import org.jetbrains.exposed.v1.core.and
import org.jetbrains.exposed.v1.r2dbc.batchInsert
import org.jetbrains.exposed.v1.r2dbc.select

class EncounterRepositoryImpl : EncounterRepository {
    override suspend fun findByID(userID: UserID, encounterDate: EncounterDate): Encounter {
        val list = EncounterTable
            .select(
                EncounterTable.id, EncounterTable.encounterId, EncounterTable.encounterDate,
            )
            .where { (EncounterTable.id eq userID.value) and (EncounterTable.encounterDate eq encounterDate.value) }
            .toList()
        return toDomain(list)
    }

    override suspend fun save(encounter: Encounter): Encounter {
        EncounterTable.batchInsert(encounter.encounterIDs, true) { encounterID ->
            // このブロックは encounter.encounterIDs の各要素に対して一度ずつ呼ばれる
            this[EncounterTable.id] = encounter.actorID.value
            this[EncounterTable.encounterDate] = encounter.encounterDate.value
            this[EncounterTable.encounterId] = encounterID.value
        }
        return encounter
    }
}