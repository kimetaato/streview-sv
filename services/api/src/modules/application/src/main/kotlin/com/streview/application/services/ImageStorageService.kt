package com.streview.application.services

import com.streview.domain.commons.UUID
import kotlinx.io.Source

data class ImageStorageConfig(
    val directory: String,
    val domain: String
)

interface ImageStorageService {
    suspend fun save(image: Source, fileName: String, imageType: ImageType)
    suspend fun generateUrl(imageUUID: UUID, imageType: ImageType): String
}

enum class ImageType(val value: String) {
    UserIcon("icons"), Review("reviews"), Store("stores")
}
