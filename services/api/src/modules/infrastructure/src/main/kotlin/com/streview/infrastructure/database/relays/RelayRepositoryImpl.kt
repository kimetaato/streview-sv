package com.streview.infrastructure.database.relays

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.fold
import com.github.michaelbull.result.runCatching
import com.streview.domain.commons.UserID
import com.streview.domain.commons.errors.DomainError
import com.streview.domain.commons.errors.TechnicalError
import com.streview.domain.relays.Relay
import com.streview.domain.relays.RelayRepository
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.singleOrNull
import kotlinx.coroutines.flow.toList
import org.jetbrains.exposed.v1.core.Op
import org.jetbrains.exposed.v1.core.and
import org.jetbrains.exposed.v1.r2dbc.batchUpsert
import org.jetbrains.exposed.v1.r2dbc.selectAll
import org.jetbrains.exposed.v1.r2dbc.upsert

class RelayRepositoryImpl : RelayRepository {
    override suspend fun findByUserIdAndReviewUUID(
        userID: String,
        reviewUUID: String
    ): Result<Relay?, DomainError> =
        runCatching {
            RelayTable
                .selectAll()
                .where { (RelayTable.userID eq userID) and (RelayTable.reviewUUID eq reviewUUID) }
                .singleOrNull()?.let { row ->
                    toDomain(row)
                }
        }.fold(
            success = { relay -> Ok(relay) },
            failure = { throwable -> Err(TechnicalError.DatabaseError(false, throwable)) }
        )
    override suspend fun findByUserIDAndIsNotRead(userID: UserID): Result<List<Relay>, DomainError> =
        runCatching {
            RelayTable
                .selectAll()
                .where { (RelayTable.userID eq userID.value) and (RelayTable.isRead eq Op.FALSE) }
                .map { row -> toDomain(row) }.toList()
        }.fold(
            success = { relays -> Ok(relays) },
            failure = { throwable -> Err(TechnicalError.DatabaseError(false, throwable)) }
        )

    override suspend fun save(relay: Relay): Result<Relay, DomainError> =
        runCatching {
            RelayTable.upsert { statement ->
                toTable(relay)(statement)
            }
        }.fold(
            success = { Ok(relay) },
            failure = { throwable -> Err(TechnicalError.DatabaseError(false, throwable)) }
        )

    override suspend fun saveAll(relays: List<Relay>): Result<List<Relay>, DomainError> =
        runCatching {
            RelayTable.batchUpsert(
                data = relays,
            ) { relay ->
                toTable(relay)
            }
        }.fold(
            success = { Ok(relays) },
            failure = { throwable -> Err(TechnicalError.DatabaseError(false, throwable)) }
        )

    override suspend fun findByUserId(userID: UserID): Result<List<Relay>, DomainError> =
        runCatching {
            RelayTable
                .selectAll()
                .where { RelayTable.userID eq userID.value }
                .map { row -> toDomain(row) }.toList()
        }.fold(
            success = { relays -> Ok(relays) },
            failure = { throwable -> Err(TechnicalError.DatabaseError(false, throwable)) }
        )

    override suspend fun findReReviewByUserId(userID: UserID): Result<List<Relay>, DomainError> =
        runCatching {
            RelayTable
                .selectAll()
                .where { (RelayTable.userID eq userID.value) and (RelayTable.isReReview eq Op.TRUE) }
                .map { row -> toDomain(row) }.toList()
        }.fold(
            success = { relays -> Ok(relays) },
            failure = { throwable -> Err(TechnicalError.DatabaseError(false, throwable)) }
        )
}
