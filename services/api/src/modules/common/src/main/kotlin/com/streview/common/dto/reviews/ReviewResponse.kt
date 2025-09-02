package com.streview.common.dto.reviews

import com.streview.common.dto.OutputPort
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * 特定レビューを再共有状態(ReReview)に設定するリクエストのレスポンス
 * @property reviewUUID レビューのUUID
 */
@Serializable
data class RelayStatusToggleResponse(
    @SerialName("review_uuid") val reviewUUID: String
) : OutputPort

@Serializable
data class PostReviewResponse(
    @SerialName("review_uuid") val reviewUUID: String
) : OutputPort

@Serializable
data class GetMyReviewsResponse(
    @SerialName("reviews") val reviews: List<Review>
) : OutputPort

@Serializable
data class GetReReviewResponse(
    @SerialName("reviews") val reviews: List<Review>
) : OutputPort

@Serializable
data class Review(
    @SerialName("review_uuid") val reviewUUID: String,
    @SerialName("comment") val comment: String,
    @SerialName("star") val star: Double,
    @SerialName("image_urls") val imageUrls: List<String>,
    @SerialName("created_at") val createdAt: LocalDateTime,
    @SerialName("updated_at") val updatedAt: LocalDateTime,
    @SerialName("store_name") val storeName: String,
    @SerialName("store_uuid") val storeUUID: String,
)
