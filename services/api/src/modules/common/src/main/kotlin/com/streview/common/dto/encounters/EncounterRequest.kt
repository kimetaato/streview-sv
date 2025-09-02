package com.streview.common.dto.encounters

import com.streview.common.dto.InputPort
import kotlinx.datetime.LocalDate
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * すれ違いリクエストのDTO
 * @property userID リクエストを送った人
 * @property encounters リクエストの内容
 */
data class EncounterRequest(
    val userID: String,
    val encounters: List<DailyEncounter>
) : InputPort

@Serializable
data class EncounterRequestJson(
    @SerialName("encounters") val encounters: List<DailyEncounter>
)

/**
 * @property date すれ違った日にち
 * @property encounter その日にすれ違ったユーザーID
 */
@Serializable
data class DailyEncounter(
    @SerialName("date") val date: LocalDate,
    @SerialName("user_ids") val encounter: List<String>
)
