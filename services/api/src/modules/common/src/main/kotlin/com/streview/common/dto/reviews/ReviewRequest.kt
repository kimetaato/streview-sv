package com.streview.common.dto.reviews

import com.streview.common.dto.InputPort
import kotlinx.io.Source
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.math.BigDecimal

@Serializable
data class RelayStatusToggleJson(
    @SerialName("status") val status: String,
)

data class PostReviewRequest(
    val userID: String,
    val storeUUID: String,
    val comment: String,
    val star: BigDecimal,
    val imageSources: List<Source>,
) : InputPort

data class GetMyReviewsRequest(
    val userID: String,
    val limit: Int = 30,
) : InputPort

data class GetReReviewRequest(
    val userID: String,
) : InputPort

/**
 * 特定レビューを再共有状態(ReReview)に設定するリクエスト
 */
data class RelayStatusToggleRequest(
    val userID: String,
    val reviewUUID: String,
    val toggleStatus: Boolean
) : InputPort
