package com.streview.infrastructure.database.users

import com.streview.domain.commons.UserID
import com.streview.domain.users.IUserRepository
import com.streview.domain.users.User
import com.streview.infrastructure.database.models.UsersTable
import kotlinx.coroutines.flow.singleOrNull
import org.jetbrains.exposed.v1.r2dbc.insert
import org.jetbrains.exposed.v1.r2dbc.select
import org.jetbrains.exposed.v1.r2dbc.transactions.suspendTransaction
import org.jetbrains.exposed.v1.r2dbc.update

class UserRepositoryImpl() : IUserRepository {
    override suspend fun findByID(userID: UserID): User? {
        val user: User? = suspendTransaction {
            UsersTable
                .select(
                    UsersTable.id, UsersTable.name, UsersTable.birthday, UsersTable.gender, UsersTable.iconUUID,
                    UsersTable.catchMode
                )
                .where { UsersTable.id eq userID.value }
                .singleOrNull()?.let { row ->
                    toDomain(row)
                }
        }
        return user
    }

    override suspend fun create(user: User): User {
        suspendTransaction {
            UsersTable.insert { statement -> // `it`を`statement`という分かりやすい名前にしています
                // toUserTable(user)で関数を取得し、
                // statement(it)を引数にしてその関数を呼び出す
                toUserTable(user)(statement)
            }
        }
        return user
    }

    override suspend fun updateProfile(user: User): User {
        UsersTable
            .update({ UsersTable.id eq user.userID.value }) {
                toUserTable(user)
            }
        return user
    }
}