package com.streview.application.usecases.encounters.dto

import com.streview.application.usecases.InputPort
import com.streview.application.usecases.OutputPort
import kotlinx.serialization.Serializable

/**
 * @param userID リクエストを送信したユーザー
 * @param encounters すれ違ったユーザーのリスト　日付: その日にすれ違ったユーザーから取得した文字列
 */
@Serializable
data class EncounterRequest(val userID:String, val encounters: Map<String, List<String>>): InputPort

@Serializable
data class EncounterResponse(val result: Boolean): OutputPort