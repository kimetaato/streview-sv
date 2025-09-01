package com.streview.infrastructure.database.images

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import com.streview.domain.commons.UUID
import com.streview.domain.commons.errors.DomainError
import com.streview.domain.commons.errors.TechnicalError
import com.streview.domain.images.Image
import com.streview.domain.images.ImageRepository
import com.streview.infrastructure.database.models.ImagesTable
import kotlinx.coroutines.flow.singleOrNull
import org.jetbrains.exposed.v1.r2dbc.insert
import org.jetbrains.exposed.v1.r2dbc.select

class ImageRepositoryImpl : ImageRepository {
    override suspend fun findByID(imageId: UUID): Result<Image?, DomainError> =
        try {
            Ok(
                ImagesTable
                    .select(
                        ImagesTable.imageUUID,
                        ImagesTable.fileName
                    )
                    .where { ImagesTable.imageUUID eq imageId.value }
                    .singleOrNull()?.let { row ->
                        toDomain(row)
                    }
            )
        } catch (e: Exception) {
            Err(TechnicalError.DatabaseError(false, e))
        }

    override suspend fun save(image: Image): Result<Image, DomainError> =
        try {
            ImagesTable.insert { statement ->
                toTable(image)(statement)
            }
            Ok(image)
        } catch (e: Exception) {
            Err(TechnicalError.DatabaseError(false, e))
        }
}
