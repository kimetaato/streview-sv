package com.streview.infrastructure.database.users

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import com.streview.domain.commons.UserID
import com.streview.domain.commons.errors.DomainError
import com.streview.domain.commons.errors.TechnicalError
import com.streview.domain.users.User
import com.streview.domain.users.UserRepository
import com.streview.infrastructure.database.models.UsersTable
import kotlinx.coroutines.flow.singleOrNull
import org.jetbrains.exposed.v1.r2dbc.insert
import org.jetbrains.exposed.v1.r2dbc.select
import org.jetbrains.exposed.v1.r2dbc.update

class UserRepositoryImpl() : UserRepository {
    override suspend fun findByID(userID: UserID): Result<User?, DomainError> =
        try {
            val user: User? =
                UsersTable
                    .select(
                        UsersTable.id,
                        UsersTable.name,
                        UsersTable.birthday,
                        UsersTable.gender,
                        UsersTable.iconUUID,
                        UsersTable.catchMode
                    )
                    .where { UsersTable.id eq userID.value }
                    .singleOrNull()?.let { row ->
                        toDomain(row)
                    }
            Ok(user)
        } catch (e: Exception) {
            Err(TechnicalError.DatabaseError(false, e))
        }

    override suspend fun save(user: User): Result<User, DomainError> =
        try {
            UsersTable.insert { statement -> // `it`を`statement`という分かりやすい名前にしています
                // toUserTable(user)で関数を取得し、
                // statement(it)を引数にしてその関数を呼び出す
                toUserTable(user)(statement)
            }
            Ok(user)
        } catch (e: Exception) {
            Err(TechnicalError.DatabaseError(false, e))
        }

    override suspend fun updateProfile(user: User): Result<User, DomainError> =
        try {
            UsersTable
                .update({ UsersTable.id eq user.userID.value }) {
                    toUserTable(user)
                }
            Ok(user)
        } catch (e: Exception) {
            Err(TechnicalError.DatabaseError(false, e))
        }
}
