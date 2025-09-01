package com.streview.infrastructure.database.images

import com.streview.domain.images.Image
import com.streview.infrastructure.database.models.ImagesTable
import org.jetbrains.exposed.v1.core.ResultRow
import org.jetbrains.exposed.v1.core.statements.UpdateBuilder

fun toDomain(row: ResultRow): Image {
    return Image.create(
        row[ImagesTable.imageUUID],
        row[ImagesTable.fileName]
    )
}

fun toTable(image: Image): (UpdateBuilder<*>) -> Unit {
    return {
        with(ImagesTable) {
            it[imageUUID] = image.imageUUID.value
            it[fileName] = image.fileName.value
        }
    }
}
