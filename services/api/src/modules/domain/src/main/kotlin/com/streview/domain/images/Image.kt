package com.streview.domain.images

import com.streview.domain.commons.UUID

data class Image(
    val imageUUID: UUID,
    val fileName: FilePath,
)

@JvmInline
value class ImageUUID(val value: String) {
    init {
        require(value.isNotEmpty()) { "無効な画像パスです。" }
    }
}

@JvmInline
value class FilePath(val value: String) {
    init {
        require(value.isNotEmpty()) { "ファイル名が不正です。" }
    }
}
