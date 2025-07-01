package com.streview.infrastructure.database.encounters

import com.streview.domain.commons.UserID
import com.streview.domain.encounts.Encounter
import com.streview.domain.encounts.EncounterRepository
import com.streview.infrastructure.database.models.EncounterTable
import kotlinx.coroutines.flow.toList
import org.jetbrains.exposed.v1.r2dbc.select
import org.jetbrains.exposed.v1.r2dbc.upsert

class EncounterRepositoryImpl: EncounterRepository {
    override suspend fun findByID(userID: UserID): Encounter {
        val list = EncounterTable
            .select(
                EncounterTable.encounterId, EncounterTable.localDate
            )
            .where { EncounterTable.encounterId eq userID.value }
            .toList()
        return toDomain(userID, list)
    }

    override suspend fun save(encounter: Encounter): Encounter {
        EncounterTable
            .upsert{
                toTable(encounter)
            }
        return encounter
    }
}