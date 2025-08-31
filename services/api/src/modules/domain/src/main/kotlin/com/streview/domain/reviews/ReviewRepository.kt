package com.streview.domain.reviews

import com.github.michaelbull.result.Result
import com.streview.domain.commons.UUID
import com.streview.domain.commons.UserID
import com.streview.domain.commons.errors.DomainError

interface ReviewRepository {
    suspend fun save(review: Review): Result<Review, DomainError>
    suspend fun findByWriterID(writerID: UserID): Result<List<Review>, DomainError>
    suspend fun findInUUIDs(uuids: List<UUID>): Result<List<Review>, DomainError>
    suspend fun findByStoreUUIDs(storeUUIDs: List<UUID>): Result<List<Review>, DomainError>
}
