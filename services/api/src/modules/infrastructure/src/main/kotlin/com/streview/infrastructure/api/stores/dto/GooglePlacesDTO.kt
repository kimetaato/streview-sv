package com.streview.infrastructure.api.stores.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GooglePlacesRequest(
    val includedType: String,
    val languageCode: String,
    val locationBias: LocationBias,
    val textQuery: String,
)

@Serializable
data class LocationBias(
    val circle: Circle,
)

@Serializable
data class Circle(
    val center: Location,
    val radius: Double,
)

@Serializable
data class Location(
    val latitude: Double,
    val longitude: Double
)

@Serializable
data class GooglePlacesResponse(
    val places: List<GooglePlace>,
)

@Serializable
data class GooglePlace(
    @SerialName("nationalPhoneNumber")
    val phoneNumber: String,
    val displayName: DisplayName,
    val location: Location,
)

@Serializable
data class DisplayName(
    val text: String,
)
