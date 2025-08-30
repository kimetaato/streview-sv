package com.streview.domain.encounters

import com.streview.domain.commons.UserID
import com.streview.domain.exceptions.DuplicateEncounterException
import com.streview.domain.exceptions.InvalidInputException
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
        fun factory(actorID: String, encounterDate: LocalDate): Encounter {
            return Encounter(
                UserID(actorID),
                EncounterDate(encounterDate),
                emptyList<UserID>().toMutableList(),
            )
        }

        fun factory(userID: String, encounterDate: LocalDate, encounterIDs: List<String>): Encounter {
            return Encounter(
                UserID(userID),
                EncounterDate(encounterDate),
                encounterIDs.map { UserID(it) }.toMutableList(),
            )
        }
    }

    // すれ違いしたユーザーを追加する
    fun add(encounterID: UserID) {
        when (encounterID) {
            actorID -> throw InvalidInputException("不正な入力値が含まれています。")
            in _encounterIDs -> throw DuplicateEncounterException("すでにすれ違っています。")
            else -> {
                // すれちがいを追加
                _encounterIDs.add(encounterID)
            }
        }
    }
}

@JvmInline
value class EncounterDate(val value: LocalDate) {
    init {
        val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
        require(value <= today) { "未来の日付です。" }
    }
}
