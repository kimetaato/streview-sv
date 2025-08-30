package com.streview.application.services

import com.streview.domain.commons.UUID
import kotlinx.io.Source

data class ImageStorageConfig(val baseDirectory: String = "/app/src/upload")

interface ImageStorageService {
    suspend fun save(image: Source, fileName: String, imageType: ImageType)
    suspend fun generateUrl(imageUUID: UUID, imageType: ImageType): String
}

enum class ImageType(val value: String) {
    UserIcon("user_icon"), Review("review"), Store("store");

    companion object {
        private val map = ImageType.entries.associateBy(ImageType::value)
        fun fromValue(type: String) = map[type] ?: throw TypeCastException("Unknown type $type")
    }
}
