package com.streview.domain.reviews

import com.github.michaelbull.result.Result
import com.streview.domain.commons.UUID
import com.streview.domain.commons.errors.DomainError

interface ReviewRepository {
    suspend fun save(review: Review): Review
    suspend fun findInUUIDs(uuids: List<UUID>): Result<List<Review>, DomainError>
}
