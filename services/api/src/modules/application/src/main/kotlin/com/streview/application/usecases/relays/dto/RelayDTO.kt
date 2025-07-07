package com.streview.usecase.relays.dto

import com.streview.application.usecases.InputPort
import com.streview.application.usecases.OutputPort
import kotlinx.serialization.Serializable

/**
 * 公開レビューを取得するリクエスト
 */
@Serializable
data class GetPublicReviewsRequest(
    val userId: String
) : InputPort

/**
 * 公開レビューを取得するレスポンス
 */
@Serializable
data class GetPublicReviewsResponse(
    val reviewIds: List<String>
) : OutputPort

/**
 * 特定レビューを「公開状態」に更新するリクエスト
 */
@Serializable
data class MarkReviewAsRereviewedRequest(
    val userId: String,
    val reviewId: String
) : InputPort

/**
 * 公開状態更新のレスポンス
 */
@Serializable
data class MarkReviewAsRereviewedResponse(
    val success: Boolean,
    val message: String
) : OutputPort
