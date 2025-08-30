package com.streview.infrastructure.api.stores.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class HotPepperResponse(
    val results: HotPepperResult
)

@Serializable
data class HotPepperResult(
    val shop: List<HotPepperShop>
)

@Serializable
data class HotPepperShop(
    val name: String,
    val address: String,
    @SerialName("logo_image")
    val logoImage: String,
    val genre: Genre,
    val photo: Photo,
    val open: String,
    val catch: String
)

@Serializable
data class Genre(
    val name: String
)

@Serializable
data class Photo(
    val mobile: MobilePhoto
)

@Serializable
data class MobilePhoto(
    val l: String,
    val s: String
)
