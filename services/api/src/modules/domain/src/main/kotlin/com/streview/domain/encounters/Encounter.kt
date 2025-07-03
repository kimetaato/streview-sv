package com.streview.domain.encounters

import com.streview.domain.commons.UserID
import com.streview.domain.exceptions.DuplicateEncounterException
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

class Encounter private constructor(
    val actorID: UserID,
    val encounterDate: EncounterDate,
    private val _encounterIDs: MutableList<UserID>,
) {
    val encounterIDs: List<UserID>
        get() = _encounterIDs.toList()

    companion object {
        fun factory(actorID: UserID, encounterDate: EncounterDate): Encounter {
            return Encounter(
                actorID,
                encounterDate,
                emptyList<UserID>().toMutableList(),
            )
        }

        fun factory(userID: UserID, encounterDate: EncounterDate, encounterIDs: List<UserID>): Encounter {
            return Encounter(
                userID,
                encounterDate,
                encounterIDs.toMutableList(),
            )
        }
    }

    // すれ違いしたユーザーを追加する
    fun add(encounterID: UserID): Encounter {
        if (encounterID == actorID) {
            return this
        }

        if (_encounterIDs.contains(encounterID)) {
            throw DuplicateEncounterException("すでにすれ違っています。:${encounterDate}")
        }

        _encounterIDs.add(encounterID)
        return this
    }

}

@JvmInline
value class EncounterDate(val value: LocalDate) {
    init {
        val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
        require(value <= today) { "未来の日付です。" }
    }
}