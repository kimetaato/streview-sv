package com.streview.domain.images

import com.streview.domain.commons.UUID


interface ImageRepository {
    suspend fun findByID(imageId: UUID): Image?
    suspend fun save(image:Image): Image
}