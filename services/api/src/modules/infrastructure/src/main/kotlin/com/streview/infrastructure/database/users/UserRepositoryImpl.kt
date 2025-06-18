package com.streview.infrastructure.database.users

import com.streview.domain.users.IUserRepository
import com.streview.domain.users.User
import com.streview.infrastructure.database.models.UsersTable
import kotlinx.coroutines.flow.singleOrNull
import org.jetbrains.exposed.v1.core.SqlExpressionBuilder.eq
import org.jetbrains.exposed.v1.r2dbc.insert
import org.jetbrains.exposed.v1.r2dbc.select
import org.jetbrains.exposed.v1.r2dbc.transactions.suspendTransaction
import org.jetbrains.exposed.v1.r2dbc.update

class UserRepositoryImpl() : IUserRepository {
    override suspend fun findById(id: Int): User? {
        val user: User? = suspendTransaction {
            UsersTable
                .select(UsersTable.id, UsersTable.name, UsersTable.email)
                .where { UsersTable.id eq id }
                .singleOrNull()?.let { it ->
                    toDomain(it)
                }
        }
        return user
    }
    suspend fun create(user: User): User {
        UsersTable.insert {it ->
            with(UsersTable) {
                it[id] = user.id.value
                it[name] = user.name.value
                it[email] = user.email.value
            }
        }
        return user
    }

    suspend fun update(user: User): User {
        UsersTable
            .update({UsersTable.id eq user.id.value}) {it ->
                toUserTable(user)
        }
        return user
    }
}