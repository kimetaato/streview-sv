package com.streview.common.dto.encounters

import com.streview.common.dto.OutputPort
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * @property result すれ違ってレビューを取得した人数
 */
@Serializable
data class EncounterResponse(
    @SerialName("result") val result: Int
) : OutputPort
