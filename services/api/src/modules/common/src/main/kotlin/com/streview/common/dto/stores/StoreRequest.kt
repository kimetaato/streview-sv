package com.streview.common.dto.stores

import com.streview.common.dto.InputPort
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

data class GetGeofenceRequest(
    val userId: String,
    val location: Location
) : InputPort

data class GetNewStoreRequest(
    val userID: String,
) : InputPort

data class CheckInRequest(
    val userID: String,
    val storeUUID: String,
) : InputPort

data class VisitStatusRequest(
    val userID: String,
    val storeUUID: String,
    val status: String
) : InputPort

@Serializable
data class Location(
    @SerialName("lat") val lat: Double,
    @SerialName("lng") val lng: Double
)

@Serializable
data class GetGeofenceRequestJson(
    @SerialName("location") val location: Location
)

@Serializable
data class VisitStatusRequestJson(
    @SerialName("status") val status: String
)
