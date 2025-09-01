package com.streview.infrastructure.database.images

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.fold
import com.github.michaelbull.result.runCatching
import com.streview.domain.commons.UUID
import com.streview.domain.commons.errors.DomainError
import com.streview.domain.commons.errors.TechnicalError
import com.streview.domain.images.Image
import com.streview.domain.images.ImageRepository
import kotlinx.coroutines.flow.singleOrNull
import org.jetbrains.exposed.v1.r2dbc.insert
import org.jetbrains.exposed.v1.r2dbc.selectAll

class ImageRepositoryImpl : ImageRepository {
    override suspend fun findByImageUUID(imageUUID: UUID): Result<Image?, DomainError> =
        runCatching {
            ImagesTable
                .selectAll()
                .where { ImagesTable.imageUUID eq imageUUID.value }
                .singleOrNull()?.let { row ->
                    toDomain(row)
                }
        }.fold(
            success = { image -> Ok(image) },
            failure = { throwable -> Err(TechnicalError.DatabaseError(false, throwable)) }
        )

    override suspend fun save(image: Image): Result<Image, DomainError> =
        runCatching {
            ImagesTable.insert { statement ->
                toTable(image)(statement)
            }
        }.fold(
            success = { Ok(image) },
            failure = { throwable -> Err(TechnicalError.DatabaseError(false, throwable)) }
        )
}
