package com.streview.infrastructure.database.relays

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import com.streview.domain.commons.UserID
import com.streview.domain.commons.errors.DomainError
import com.streview.domain.commons.errors.TechnicalError
import com.streview.domain.relays.Relay
import com.streview.domain.relays.RelayRepository
import com.streview.infrastructure.database.models.RelaysTable
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.singleOrNull
import kotlinx.coroutines.flow.toList
import org.jetbrains.exposed.v1.core.Op
import org.jetbrains.exposed.v1.core.SqlExpressionBuilder.eq
import org.jetbrains.exposed.v1.core.and
import org.jetbrains.exposed.v1.r2dbc.batchUpsert
import org.jetbrains.exposed.v1.r2dbc.selectAll
import org.jetbrains.exposed.v1.r2dbc.upsert

class RelayRepositoryImpl : RelayRepository {
    override suspend fun findByUserIdAndReviewUUID(
        userID: String,
        reviewUUID: String
    ): Result<Relay?, DomainError> =
        try {
            Ok(
                RelaysTable
                    .selectAll()
                    .where((RelaysTable.userID eq userID) and (RelaysTable.reviewUUID eq reviewUUID))
                    .singleOrNull()?.let { row ->
                        toDomain(row)
                    }
            )
        } catch (e: Exception) {
            Err(TechnicalError.DatabaseError(false, e))
        }

    override suspend fun findByUserIDAndIsNotRead(userID: UserID): Result<List<Relay>, DomainError> =
        try {
            Ok(
                RelaysTable
                    .selectAll()
                    .where { (RelaysTable.userID eq userID.value) and (RelaysTable.isRead eq false) }
                    .map { row -> toDomain(row) }.toList()
            )
        } catch (e: Exception) {
            Err(TechnicalError.DatabaseError(false, e))
        }

    override suspend fun save(relay: Relay): Result<Relay, DomainError> =
        try {
            RelaysTable.upsert { statement ->
                toTable(relay)(statement)
            }
            Ok(relay)
        } catch (e: Exception) {
            Err(TechnicalError.DatabaseError(false, e))
        }

    override suspend fun saveAll(relays: List<Relay>): Result<List<Relay>, DomainError> =
        try {
            RelaysTable.batchUpsert(
                data = relays,
            ) { relay ->
                toTable(relay)
            }

            Ok(relays)
        } catch (e: Exception) {
            Err(TechnicalError.DatabaseError(false, e))
        }

    override suspend fun findAllByUserId(userID: UserID): Result<List<Relay>, DomainError> =
        try {
            Ok(
                RelaysTable
                    .selectAll()
                    .where(
                        RelaysTable.userID eq userID.value
                    )
                    .map { row -> toDomain(row) }.toList()
            )
        } catch (e: Exception) {
            Err(TechnicalError.DatabaseError(false, e))
        }

    override suspend fun findReReviewByUserId(userID: UserID): Result<List<Relay>, DomainError> =
        try {
            Ok(
                RelaysTable
                    .selectAll()
                    .where(
                        RelaysTable.userID eq userID.value and RelaysTable.isReReviewed eq Op.TRUE
                    )
                    .map { row -> toDomain(row) }.toList()
            )
        } catch (e: Exception) {
            Err(TechnicalError.DatabaseError(false, e))
        }
}
