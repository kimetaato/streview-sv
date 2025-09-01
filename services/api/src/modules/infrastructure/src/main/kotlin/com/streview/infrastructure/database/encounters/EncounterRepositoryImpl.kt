package com.streview.infrastructure.database.encounters

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import com.streview.domain.commons.errors.DomainError
import com.streview.domain.commons.errors.TechnicalError
import com.streview.domain.encounters.Encounter
import com.streview.domain.encounters.EncounterRepository
import com.streview.infrastructure.database.models.EncounterTable
import kotlinx.coroutines.flow.toList
import kotlinx.datetime.LocalDate
import org.jetbrains.exposed.v1.core.and
import org.jetbrains.exposed.v1.r2dbc.batchInsert
import org.jetbrains.exposed.v1.r2dbc.select

class EncounterRepositoryImpl : EncounterRepository {

    override suspend fun findByID(userID: String, encounterDate: LocalDate): Result<Encounter?, DomainError> {
        val list = EncounterTable
            .select(
                EncounterTable.userID,
                EncounterTable.encounterId,
                EncounterTable.encounterDate,
            )
            .where { (EncounterTable.userID eq userID) and (EncounterTable.encounterDate eq encounterDate) }
            .toList()
        return Ok(toDomain(list))
    }

    override suspend fun save(encounter: Encounter): Result<Encounter, DomainError> =
        try {
            EncounterTable.batchInsert(encounter.encounterIDs, true) { encounterID ->
                // このブロックは encounter.encounterIDs の各要素に対して一度ずつ呼ばれる
                this[EncounterTable.userID] = encounter.actorID.value
                this[EncounterTable.encounterDate] = encounter.encounterDate.value
                this[EncounterTable.encounterId] = encounterID.value
            }
            Ok(encounter)
        } catch (e: Exception) {
            Err(TechnicalError.DatabaseError(false, e))
        }
}
