package com.streview.domain.encounters

import com.streview.domain.commons.UserID

interface EncounterRepository {
    suspend fun findByID(userID: UserID, encounterDate: EncounterDate): Encounter
    suspend fun save(encounter: Encounter): Encounter
}