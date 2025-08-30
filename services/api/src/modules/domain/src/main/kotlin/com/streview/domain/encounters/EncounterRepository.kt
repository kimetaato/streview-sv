package com.streview.domain.encounters

import com.github.michaelbull.result.Result
import com.streview.domain.commons.errors.DomainError
import kotlinx.datetime.LocalDate

interface EncounterRepository {
    suspend fun findByID(userID: String, encounterDate: LocalDate): Result<Encounter?, DomainError>
    suspend fun save(encounter: Encounter): Result<Encounter, DomainError>
}
