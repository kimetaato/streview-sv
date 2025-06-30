package com.streview.domain.encounts

import com.streview.domain.commons.UserID
import kotlinx.datetime.LocalDate

class Encounter private constructor(
    val actorID: UserID,
    val encountersByDay: Map<LocalDate, List<UserID>>,
) {
    companion object {
        fun factory(userID: UserID): Encounter {
           return  Encounter(userID, emptyMap())
        }

        fun factory(userID: UserID, encountersByDay: Map<LocalDate, List<UserID>>): Encounter {
            return Encounter(userID, encountersByDay)
        }
    }

    // すれ違いしたユーザーを追加する
    fun add(date: LocalDate, userID: UserID): Encounter {
        // 引数で指定した日にすれ違った人のリストを取得
        val currentList = encountersByDay[date] ?: emptyList()

        // リストに対してすれ違った対象のユーザーを追加する
        val newUserList = currentList.plus(userID)

        // 作成したユーザーIDのリストを追加したマップをコピーして新規作成する
        val newEncountersByDay = encountersByDay.plus(date to newUserList
        )

        return Encounter(
            actorID = actorID,
            encountersByDay = newEncountersByDay
        )
    }
}