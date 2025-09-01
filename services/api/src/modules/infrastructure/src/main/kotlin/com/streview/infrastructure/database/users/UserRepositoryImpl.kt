package com.streview.infrastructure.database.users

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.fold
import com.github.michaelbull.result.runCatching
import com.streview.domain.commons.UserID
import com.streview.domain.commons.errors.DomainError
import com.streview.domain.commons.errors.TechnicalError
import com.streview.domain.users.User
import com.streview.domain.users.UserRepository
import kotlinx.coroutines.flow.singleOrNull
import org.jetbrains.exposed.v1.r2dbc.insert
import org.jetbrains.exposed.v1.r2dbc.selectAll
import org.jetbrains.exposed.v1.r2dbc.update

class UserRepositoryImpl() : UserRepository {
    override suspend fun findByID(userID: UserID): Result<User?, DomainError> =
        runCatching {
            UserTable
                .selectAll()
                .where { UserTable.userID eq userID.value }
                .singleOrNull()?.let { row ->
                    toDomain(row)
                }
        }.fold(
            success = { user -> Ok(user) },
            failure = { Err(TechnicalError.DatabaseError(false, it)) }
        )

    override suspend fun save(user: User): Result<User, DomainError> =
        runCatching {
            UserTable.insert { statement ->
                toUserTable(user)(statement)
            }
        }.fold(
            success = { Ok(user) },
            failure = { throwable -> Err(TechnicalError.DatabaseError(false, throwable)) }
        )

    override suspend fun updateProfile(user: User): Result<User, DomainError> =
        runCatching {
            UserTable
                .update({ UserTable.userID eq user.userID.value }) {
                    toUserTable(user)
                }
        }.fold(
            success = { Ok(user) },
            failure = { throwable -> Err(TechnicalError.DatabaseError(false, throwable)) }
        )
}
