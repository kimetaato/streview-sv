package com.streview.domain.images

import com.streview.domain.commons.UUID

class Image private constructor(
    val imageUUID: UUID,
    val fileName: FileName,
) {
    companion object {
        fun create(fileName: String):Image {
            return Image(
                imageUUID = UUID.generate(),
                fileName = FileName(fileName),
            )
        }
        fun create(imageUUID: String, fileName: String): Image {
            return Image(
                imageUUID = UUID.generate(imageUUID),
                fileName = FileName(fileName)
            )
        }
    }
}

@JvmInline
value class FileName(val value: String) {
    init {
        require(value.isNotEmpty()) { "ファイル名が不正です。" }
    }
}
