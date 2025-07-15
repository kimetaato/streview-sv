package com.streview.domain.encounters

import kotlinx.datetime.LocalDate

interface EncounterRepository {
    suspend fun findByID(userID: String, encounterDate: LocalDate): Encounter
    suspend fun save(encounter: Encounter): Encounter
}