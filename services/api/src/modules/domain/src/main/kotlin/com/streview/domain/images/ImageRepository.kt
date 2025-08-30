package com.streview.domain.images

import com.github.michaelbull.result.Result
import com.streview.domain.commons.UUID
import com.streview.domain.commons.errors.DomainError

interface ImageRepository {
    suspend fun findByID(imageId: UUID): Result<Image?, DomainError>
    suspend fun save(image: Image): Result<Image, DomainError>
}
