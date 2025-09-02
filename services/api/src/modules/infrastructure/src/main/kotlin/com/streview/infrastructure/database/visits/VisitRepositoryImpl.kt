package com.streview.infrastructure.database.visits

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.fold
import com.github.michaelbull.result.runCatching
import com.streview.domain.commons.UUID
import com.streview.domain.commons.UserID
import com.streview.domain.commons.errors.DomainError
import com.streview.domain.commons.errors.TechnicalError
import com.streview.domain.visits.Visit
import com.streview.domain.visits.VisitRepository
import com.streview.domain.visits.vo.Status
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.singleOrNull
import kotlinx.coroutines.flow.toList
import org.jetbrains.exposed.v1.core.and
import org.jetbrains.exposed.v1.r2dbc.insert
import org.jetbrains.exposed.v1.r2dbc.selectAll

class VisitRepositoryImpl : VisitRepository {
    override suspend fun save(visit: Visit): Result<Visit, DomainError> =
        runCatching {
            VisitTable.insert { statement ->
                toTable(visit)(statement)
            }
        }.fold(
            success = { Ok(visit) },
            failure = { throwable -> Err(TechnicalError.DatabaseError(false, throwable)) }
        )

    override suspend fun findByUserIDAndWant(userID: UserID): Result<List<Visit>, DomainError> =
        runCatching {
            VisitTable
                .selectAll()
                .where { (VisitTable.userID eq userID.value) and (VisitTable.status eq Status.Wanted.value) }
                .map { row -> toDomain(row) }.toList()
        }.fold(
            success = { visits -> Ok(visits) },
            failure = { throwable -> Err(TechnicalError.DatabaseError(false, throwable)) }
        )

    override suspend fun findByUserIDAndNeutral(userID: UserID): Result<List<Visit>, DomainError> =
        runCatching {
            VisitTable
                .selectAll()
                .where { (VisitTable.userID eq userID.value) and (VisitTable.status eq Status.Neutral.value) }
                .map { row -> toDomain(row) }.toList()
        }.fold(
            success = { visits -> Ok(visits) },
            failure = { throwable -> Err(TechnicalError.DatabaseError(false, throwable)) }
        )

    override suspend fun findByUserIDAndStoreUUID(userID: UserID, storeUUID: UUID): Result<Visit?, DomainError> =
        runCatching {
            VisitTable
                .selectAll()
                .where { (VisitTable.userID eq userID.value) and (VisitTable.storeUUID eq storeUUID.value) }
                .singleOrNull()?.let { row -> toDomain(row) }
        }.fold(
            success = { visit -> Ok(visit) },
            failure = { throwable -> Err(TechnicalError.DatabaseError(false, throwable)) }
        )
}
