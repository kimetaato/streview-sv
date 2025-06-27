package com.streview.application.services

import kotlinx.io.Source

data class ImageStorageConfig(val directories: Map<String, String>)

interface ImageStorageService {
    suspend fun save(image: Source, fileName: String, imageType: ImageType)
}

enum class ImageType(val value: String) {
    UserIcon("user_icon"), Review("review"), Store("store");

    companion object {
        private val map = ImageType.values().associateBy(ImageType::value)
        fun fromValue(type: String) = map[type] ?: throw TypeCastException("Unknown type $type")
    }
}
