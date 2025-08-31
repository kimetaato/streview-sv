package com.streview.infrastructure.database.reviews

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import com.streview.domain.commons.UUID
import com.streview.domain.commons.UserID
import com.streview.domain.commons.errors.DomainError
import com.streview.domain.commons.errors.TechnicalError
import com.streview.domain.reviews.Review
import com.streview.domain.reviews.ReviewRepository
import com.streview.infrastructure.database.models.ReviewTable
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import org.jetbrains.exposed.v1.core.SqlExpressionBuilder.eq
import org.jetbrains.exposed.v1.core.SqlExpressionBuilder.inList
import org.jetbrains.exposed.v1.r2dbc.insert
import org.jetbrains.exposed.v1.r2dbc.select
import kotlin.collections.map

class ReviewRepositoryImpl : ReviewRepository {
    override suspend fun save(review: Review): Result<Review, DomainError> =
        try {
            ReviewTable.insert { statement ->
                toTable(review)(statement)
            }
            Ok(review)
        } catch (e: Exception) {
            Err(TechnicalError.DatabaseError(false, e))
        }

    override suspend fun findByWriterID(writerID: UserID): Result<List<Review>, DomainError> =
        try {
            Ok(
                ReviewTable
                    .select(
                        ReviewTable.id,
                        ReviewTable.writerId,
                        ReviewTable.storeUUID,
                        ReviewTable.comment,
                        ReviewTable.star,
                        ReviewTable.imageUUIDs,
                        ReviewTable.isPublic,
                        ReviewTable.createdAt,
                        ReviewTable.updatedAt
                    )
                    .where(
                        ReviewTable.writerId eq writerID.value
                    )
                    .map { row -> toDomain(row) }.toList()
            )
        } catch (e: Exception) {
            Err(TechnicalError.DatabaseError(false, e))
        }

    override suspend fun findInUUIDs(uuids: List<UUID>): Result<List<Review>, DomainError> =
        try {
            Ok(
                ReviewTable
                    .select(
                        ReviewTable.id,
                        ReviewTable.writerId,
                        ReviewTable.storeUUID,
                        ReviewTable.comment,
                        ReviewTable.star,
                        ReviewTable.imageUUIDs,
                        ReviewTable.isPublic,
                        ReviewTable.createdAt,
                        ReviewTable.updatedAt
                    )
                    .where(
                        ReviewTable.id inList uuids.map { it.value }
                    )
                    .map { row -> toDomain(row) }.toList()
            )
        } catch (e: Exception) {
            Err(TechnicalError.DatabaseError(false, e))
        }

    override suspend fun findByStoreUUIDs(storeUUIDs: List<UUID>): Result<List<Review>, DomainError> =
        try {
            Ok(
                ReviewTable
                    .select(
                        ReviewTable.id,
                        ReviewTable.writerId,
                        ReviewTable.storeUUID,
                        ReviewTable.comment,
                        ReviewTable.star,
                        ReviewTable.imageUUIDs,
                        ReviewTable.isPublic,
                        ReviewTable.createdAt,
                        ReviewTable.updatedAt
                    )
                    .where(
                        ReviewTable.storeUUID inList storeUUIDs.map { it.value }
                    )
                    .map { row -> toDomain(row) }.toList()
            )
        } catch (e: Exception) {
            Err(TechnicalError.DatabaseError(false, e))
        }
}
