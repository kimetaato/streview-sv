package com.streview.application.usecases.stores.dto

import com.streview.application.usecases.InputPort
import com.streview.application.usecases.OutputPort
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable

enum class SortBy(name: String) {
    NearBy("near_by"),
    Rating("rating"),
    ReviewCount("review_count")
}

@Serializable
data class GetGeofenceRequest(
    val userId: String,
    val location: Location
) : InputPort

@Serializable
data class Location(
    val lat: Double,
    val lng: Double
)

@Serializable
data class GetGeofenceResponse(
    val stores: List<StoreHeader>,
    val range: Double
) : OutputPort

@Serializable
data class StoreHeader(
    val uuid: String,
    val name: String,
    val lat: Double,
    val lng: Double,
)

@Serializable
data class GetNewStoreRequest(
    val userID: String,
) : InputPort

@Serializable
data class GetNewStoreResponse(
    val stores: List<StoreRes>
) : OutputPort

@Serializable
data class StoreRes(
    val storeUUID: String,
    val name: String,
    val genre: String,
    val address: String,
    val tel: String,
    val description: String,
    val open: String,
    val reviews: List<ReviewRes>
)

@Serializable
data class ReviewRes(
    val reviewUUID: String,
    val comment: String,
    val star: Double,
    val createdAt: LocalDateTime,
    val imageUrls: List<String>
)
