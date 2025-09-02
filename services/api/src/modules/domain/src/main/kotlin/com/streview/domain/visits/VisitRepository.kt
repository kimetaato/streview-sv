package com.streview.domain.visits

import com.github.michaelbull.result.Result
import com.streview.domain.commons.UUID
import com.streview.domain.commons.UserID
import com.streview.domain.commons.errors.DomainError

interface VisitRepository {
    suspend fun save(visit: Visit): Result<Visit, DomainError>
    suspend fun findByUserIDAndWant(userID: UserID): Result<List<Visit>, DomainError>
    suspend fun findByUserIDAndNeutral(userID: UserID): Result<List<Visit>, DomainError>
    suspend fun findByUserIDAndStoreUUID(userID: UserID, storeUUID: UUID): Result<Visit?, DomainError>
}
