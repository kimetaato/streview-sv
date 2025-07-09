package com.streview.application.usecases.encounters.dto

import com.streview.application.usecases.InputPort
import com.streview.application.usecases.OutputPort
import kotlinx.serialization.Serializable

/**
 * @property userID リクエストを送信したユーザー
 * @property encounters すれ違ったユーザーのクラス
 */
@Serializable
data class EncounterRequest(val userID: String, val encounters: List<DailyEncounter>) : InputPort
@Serializable
data class DailyEncounter(val date: String, val encounter: List<String>)

/**
 * @property result 処理が成功したらtrue
 */
@Serializable
data class EncounterResponse(val result: Boolean): OutputPort