package com.streview.infrastructure.database.reviews

import com.streview.domain.reviews.CompletedReview
import com.streview.domain.reviews.Review
import com.streview.infrastructure.database.models.ReviewTable
import org.jetbrains.exposed.v1.core.ResultRow
import org.jetbrains.exposed.v1.core.statements.UpdateBuilder

fun toDomain(row: ResultRow) =
    Review.reconstruct(
        reviewUUID = row[ReviewTable.reviewUUID],
        writerID = row[ReviewTable.writerId],
        storeUUID = row[ReviewTable.storeUUID],
        completedReview = CompletedReview.reconstruct(
            comment = row[ReviewTable.comment],
            star = row[ReviewTable.star],
            imageUUIDs = row[ReviewTable.imageUUIDs],
            isPublic = row[ReviewTable.isPublic],
            createdAt = row[ReviewTable.createdAt],
            updatedAt = row[ReviewTable.updatedAt]
        )
    )

fun toTable(review: Review): (UpdateBuilder<*>) -> Unit {
    return {
        with(ReviewTable) {
            it[reviewUUID] = review.reviewUUID.value
            it[writerId] = review.writerID.value
            it[storeUUID] = review.storeUUID.value
            it[comment] = review.completedReview.comment.value
            it[star] = review.completedReview.star.value
            it[imageUUIDs] = review.completedReview.imageUUIDs.map { uuid -> uuid.value }
            it[isPublic] = review.completedReview.isPublic
            it[createdAt] = review.completedReview.createdAt
            it[updatedAt] = review.completedReview.updatedAt
        }
    }
}
