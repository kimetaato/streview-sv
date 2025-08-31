package com.streview.infrastructure.database.visits

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import com.streview.domain.commons.UUID
import com.streview.domain.commons.UserID
import com.streview.domain.commons.errors.DomainError
import com.streview.domain.commons.errors.TechnicalError
import com.streview.domain.visits.Visit
import com.streview.domain.visits.VisitRepository
import com.streview.domain.visits.vo.Status
import com.streview.infrastructure.database.models.VisitTable
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.singleOrNull
import kotlinx.coroutines.flow.toList
import org.jetbrains.exposed.v1.core.and
import org.jetbrains.exposed.v1.r2dbc.insert
import org.jetbrains.exposed.v1.r2dbc.select
import org.jetbrains.exposed.v1.r2dbc.selectAll

class VisitRepositoryImpl : VisitRepository {
    override suspend fun save(visit: Visit): Result<Visit, DomainError> =
        try {
            VisitTable.insert { statement ->
                toTable(visit)(statement)
            }
            Ok(visit)
        } catch (e: Exception) {
            Err(TechnicalError.DatabaseError(false, e))
        }

    override suspend fun findByUserID(userID: UserID): Result<List<Visit>, DomainError> =
        try {
            Ok(
                VisitTable
                    .selectAll()
                    .where { VisitTable.userID eq userID.value }
                    .map { row -> toDomain(row) }.toList()
            )
        } catch (e: Exception) {
            Err(TechnicalError.DatabaseError(false, e))
        }

    override suspend fun findByUserIDAndWant(userID: UserID): Result<List<Visit>, DomainError> =
        try {
            Ok(
                VisitTable
                    .selectAll()
                    .where { (VisitTable.userID eq userID.value) and (VisitTable.status eq Status.Wanted.name) }
                    .map { row -> toDomain(row) }.toList()
            )
        } catch (e: Exception) {
            Err(TechnicalError.DatabaseError(false, e))
        }

    override suspend fun findByUserIDAndNeutral(userID: UserID): Result<List<Visit>, DomainError> =
        try {
            Ok(
                VisitTable
                    .selectAll()
                    .where { (VisitTable.userID eq userID.value) and (VisitTable.status eq Status.Neutral.name) }
                    .map { row -> toDomain(row) }.toList()
            )
        } catch (e: Exception) {
            Err(TechnicalError.DatabaseError(false, e))
        }

    override suspend fun findByUserIDAndStoreUUID(
        userID: UserID,
        storeUUID: UUID
    ): Result<Visit?, DomainError> =
        try {
            Ok(
                VisitTable.select(
                    VisitTable.userID,
                    VisitTable.storeUUID,
                    VisitTable.visitCount,
                    VisitTable.status
                ).where { (VisitTable.userID eq userID.value) and (VisitTable.storeUUID eq storeUUID.value) }
                    .singleOrNull()?.let { row -> toDomain(row) }
            )
        } catch (e: Exception) {
            Err(TechnicalError.DatabaseError(false, e))
        }
}
