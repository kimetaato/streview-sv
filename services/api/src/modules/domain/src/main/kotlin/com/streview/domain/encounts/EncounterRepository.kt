package com.streview.domain.encounts

import com.streview.domain.commons.UserID

interface EncounterRepository {
    suspend fun findByID(userID: UserID): Encounter
    suspend fun save(encounter: Encounter): Encounter
}