package com.streview.application.usecases.reviews.dto

import com.streview.application.usecases.InputPort
import com.streview.application.usecases.OutputPort
import kotlinx.datetime.LocalDateTime
import kotlinx.io.Source
import kotlinx.serialization.Serializable
import java.math.BigDecimal

data class PostReviewRequest(
    val userID: String,
    val storeUUID: String,
    val comment: String,
    val star: BigDecimal,
    val imageSources: List<Source>,
) : InputPort

@Serializable
data class PostReviewResponse(
    val reviewUUID: String
) : OutputPort

data class GetMyReviewsRequest(
    val userID: String,
    val limit: Int = 30,
) : InputPort

@Serializable
data class GetMyReviewsResponse(
    val reviews: List<Review>
) : OutputPort

@Serializable
data class Review(
    val reviewUUID: String,
    val comment: String,
    val star: Double,
    val imageUrls: List<String>,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
    val storeName: String,
    val storeUUID: String,
)

data class GetReReviewRequest(
    val userID: String,
) : InputPort

@Serializable
data class GetReReviewResponse(
    val reviews: List<Review>
) : OutputPort
