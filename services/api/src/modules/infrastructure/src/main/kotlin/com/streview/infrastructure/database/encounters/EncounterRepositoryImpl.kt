package com.streview.infrastructure.database.encounters

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.fold
import com.github.michaelbull.result.map
import com.github.michaelbull.result.runCatching
import com.streview.domain.commons.errors.DomainError
import com.streview.domain.commons.errors.TechnicalError
import com.streview.domain.encounters.Encounter
import com.streview.domain.encounters.EncounterRepository
import kotlinx.coroutines.flow.toList
import kotlinx.datetime.LocalDate
import org.jetbrains.exposed.v1.core.and
import org.jetbrains.exposed.v1.r2dbc.batchInsert
import org.jetbrains.exposed.v1.r2dbc.selectAll

class EncounterRepositoryImpl : EncounterRepository {

    override suspend fun findByUserIDAndEncounterDate(
        userID: String,
        encounterDate: LocalDate
    ): Result<Encounter?, DomainError> =
        runCatching {
            EncounterTable
                .selectAll()
                .where { (EncounterTable.userID eq userID) and (EncounterTable.encounterDate eq encounterDate) }
                .toList()
        }.map { rows ->
            toDomain(rows)
        }.fold(
            success = { encounter -> Ok(encounter) },
            failure = { throwable -> Err(TechnicalError.DatabaseError(false, throwable)) }
        )

    override suspend fun save(encounter: Encounter): Result<Encounter, DomainError> =
        runCatching {
            EncounterTable
                .batchInsert(
                    data = encounter.encounterIDs
                ) { encounterID ->
                    toTable(encounter, encounterID)
                }
        }.fold(
            success = { Ok(encounter) },
            failure = { throwable -> Err(TechnicalError.DatabaseError(false, throwable)) }
        )
}
