package com.streview.domain.encounters

import com.streview.domain.commons.UserID
import com.streview.domain.commons.event.DomainEvent

data class EncounterAddDomainEvent(
    val actorID: UserID,
    val encounterDate: EncounterDate,
    val encounterID: UserID,
) : DomainEvent()