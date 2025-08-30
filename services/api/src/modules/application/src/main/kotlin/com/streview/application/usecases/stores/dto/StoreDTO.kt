package com.streview.application.usecases.stores.dto

import com.streview.application.usecases.InputPort
import com.streview.application.usecases.OutputPort
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
