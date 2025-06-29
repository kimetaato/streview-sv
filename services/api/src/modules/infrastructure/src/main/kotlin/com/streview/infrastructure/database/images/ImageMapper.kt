package com.streview.infrastructure.database.images

import com.streview.domain.images.Image
import com.streview.domain.images.ImageRepository
import com.streview.infrastructure.database.models.ImagesTable
import org.jetbrains.exposed.v1.core.ResultRow
import org.jetbrains.exposed.v1.core.statements.UpdateBuilder


fun ImageRepository.toDomain(row: ResultRow): Image {
    return Image.create(
        row[ImagesTable.id],
        row[ImagesTable.fileName]
    )
}

fun ImageRepository.toTable(image:Image): (UpdateBuilder<*>) -> Unit {
    return {
        with(ImagesTable) {
            it[id] = image.imageUUID.value
            it[fileName] = image.fileName.value
        }
    }
}