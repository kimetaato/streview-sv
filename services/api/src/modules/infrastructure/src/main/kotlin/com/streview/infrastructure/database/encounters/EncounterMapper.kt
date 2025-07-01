package com.streview.infrastructure.database.encounters

import com.streview.domain.commons.UserID
import com.streview.domain.encounts.Encounter
import com.streview.domain.encounts.EncounterRepository
import com.streview.infrastructure.database.models.EncounterTable
import kotlinx.datetime.LocalDate
import org.jetbrains.exposed.v1.core.ResultRow
import org.jetbrains.exposed.v1.core.statements.UpdateBuilder


fun EncounterRepositoryImpl.toDomain(userID: UserID, list: List<ResultRow>): Encounter {
    val encountersOfDay = mutableMapOf<LocalDate, MutableList<UserID>>()
    
    list.forEach { item ->
            val date = item[EncounterTable.localDate]
            val encounterId = item[EncounterTable.encounterId]
            encountersOfDay.getOrPut(date) { mutableListOf() }
                .add(UserID(encounterId))
    }
    
    return Encounter.factory(
        userID,
        encountersOfDay.mapValues { it.value.toList() }
    )
}

fun EncounterRepository.toTable(encounter: Encounter): List<(UpdateBuilder<*>) -> Unit> {
    // 各遭遇をそれぞれ個別のレコードとして作成
    return encounter.encountersByDay.flatMap { (date, list) ->
        list.map { userID ->
            {
                it[EncounterTable.id] = encounter.actorID.value
                it[EncounterTable.localDate] = date
                it[EncounterTable.encounterId] = userID.value
            }
        }
    }
}