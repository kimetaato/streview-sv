package com.streview.common.dto.stores

import com.streview.common.dto.OutputPort
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GetGeofenceResponse(
    @SerialName("stores") val stores: List<StoreHeader>,
    @SerialName("range") val range: Int
) : OutputPort

@Serializable
data class StoreHeader(
    @SerialName("store_uuid") val storeUUID: String,
    @SerialName("store_name") val storeName: String,
    @SerialName("location") val location: Location,
)

@Serializable
data class GetNewStoreResponse(
    @SerialName("stores") val stores: List<StoreRes>
) : OutputPort

@Serializable
data class StoreRes(
    @SerialName("store_uuid") val storeUUID: String,
    @SerialName("store_name") val name: String,
    @SerialName("genre") val genre: String,
    @SerialName("address") val address: String,
    @SerialName("tel") val tel: String,
    @SerialName("description") val description: String,
    @SerialName("open") val open: String,
    @SerialName("reviews") val reviews: List<ReviewRes>
)

@Serializable
data class ReviewRes(
    @SerialName("review_uuid") val reviewUUID: String,
    @SerialName("comment") val comment: String,
    @SerialName("star") val star: Double,
    @SerialName("created_at") val createdAt: LocalDateTime,
    @SerialName("image_urls") val imageUrls: List<String>
)

@Serializable
data class CheckInResponse(
    @SerialName("store_uuid") val storeUUID: String
) : OutputPort

@Serializable
data class VisitStatusResponse(
    @SerialName("store_uuid") val storeUUID: String
) : OutputPort
