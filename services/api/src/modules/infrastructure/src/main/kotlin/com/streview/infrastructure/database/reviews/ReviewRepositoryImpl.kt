package com.streview.infrastructure.database.reviews

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.fold
import com.github.michaelbull.result.runCatching
import com.streview.domain.commons.UUID
import com.streview.domain.commons.UserID
import com.streview.domain.commons.errors.DomainError
import com.streview.domain.commons.errors.TechnicalError
import com.streview.domain.reviews.Review
import com.streview.domain.reviews.ReviewRepository
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.singleOrNull
import kotlinx.coroutines.flow.toList
import org.jetbrains.exposed.v1.r2dbc.insert
import org.jetbrains.exposed.v1.r2dbc.selectAll
import kotlin.collections.map

class ReviewRepositoryImpl : ReviewRepository {
    override suspend fun save(review: Review): Result<Review, DomainError> =
        runCatching {
            ReviewTable.insert { statement ->
                toTable(review)(statement)
            }
        }.fold(
            success = { Ok(review) },
            failure = { throwable -> Err(TechnicalError.DatabaseError(false, throwable)) }
        )

    override suspend fun findByWriterID(writerID: UserID): Result<List<Review>, DomainError> =
        runCatching {
            ReviewTable
                .selectAll()
                .where { ReviewTable.writerId eq writerID.value }
                .map { row -> toDomain(row) }.toList()
        }.fold(
            success = { reviews -> Ok(reviews) },
            failure = { throwable -> Err(TechnicalError.DatabaseError(false, throwable)) }
        )

    override suspend fun findByReviewUUID(reviewUUID: UUID): Result<Review?, DomainError> =
        runCatching {
            ReviewTable
                .selectAll()
                .where { ReviewTable.reviewUUID eq reviewUUID.value }
                .singleOrNull()?.let { row ->
                    toDomain(row)
                }
        }.fold(
            success = { review -> Ok(review) },
            failure = { throwable -> Err(TechnicalError.DatabaseError(false, throwable)) }
        )

    override suspend fun findInReviewUUIDs(reviewUUIDs: List<UUID>): Result<List<Review>, DomainError> =
        runCatching {
            ReviewTable
                .selectAll()
                .where { ReviewTable.reviewUUID inList reviewUUIDs.map { it.value } }
                .map { row -> toDomain(row) }.toList()
        }.fold(
            success = { reviews -> Ok(reviews) },
            failure = { throwable -> Err(TechnicalError.DatabaseError(false, throwable)) }
        )

    override suspend fun findInStoreUUIDs(storeUUIDs: List<UUID>): Result<List<Review>, DomainError> =
        runCatching {
            ReviewTable
                .selectAll()
                .where { ReviewTable.storeUUID inList storeUUIDs.map { it.value } }
                .map { row -> toDomain(row) }.toList()
        }.fold(
            success = { reviews -> Ok(reviews) },
            failure = { throwable -> Err(TechnicalError.DatabaseError(false, throwable)) }
        )
}
