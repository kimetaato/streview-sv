package com.streview.application.usecases.encounters.dto

import com.streview.application.usecases.InputPort
import com.streview.application.usecases.OutputPort
import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable

/**
 * @param date すれ違った日にち
 * @param encounter その日にすれ違ったユーザーの暗号化文字列
 */
@Serializable
data class DailyEncounter(val date: LocalDate, val encounter: List<String>)

/**
 * すれ違いリクエストのDTO
 * @property userID リクエストを送った人
 * @property encounters リクエストの内容
 */
@Serializable
data class EncounterRequest(val userID: String, val encounters: List<DailyEncounter>) : InputPort

@Serializable
data class EncounterResponse(val encounterCount: Int) : OutputPort
