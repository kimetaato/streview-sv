package com.streview.infrastructure.database.images

import com.streview.domain.commons.UUID
import com.streview.domain.images.Image
import com.streview.domain.images.ImageRepository
import com.streview.infrastructure.database.models.ImagesTable
import kotlinx.coroutines.flow.singleOrNull
import org.jetbrains.exposed.v1.r2dbc.insert
import org.jetbrains.exposed.v1.r2dbc.select

class ImageRepositoryImpl: ImageRepository {
    override suspend fun findByID(imageId: UUID): Image? {
        return ImagesTable
            .select(
                ImagesTable.id, ImagesTable.fileName
            )
            .where{ ImagesTable.id eq imageId.value }
            .singleOrNull()?.let {row ->
                toDomain(row)
            }
    }

    override suspend fun save(image: Image): Image {
        ImagesTable.insert { statement ->
            toTable(image) (statement)
        }
        return image
    }
}