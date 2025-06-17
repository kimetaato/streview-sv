package com.streview.infrastructure.database.users

import com.streview.domain.users.IUserRepository
import com.streview.domain.users.User
import com.streview.infrastructure.database.models.UsersTable
import kotlinx.coroutines.flow.singleOrNull
import org.jetbrains.exposed.v1.r2dbc.select
import org.jetbrains.exposed.v1.r2dbc.transactions.suspendTransaction

class UserRepositoryImpl() : IUserRepository {
    override suspend fun findById(id: Int): User? {
        val user: User? = suspendTransaction {
            UsersTable
                .select(UsersTable.id, UsersTable.name, UsersTable.email)
                .where { UsersTable.id eq id }
                .singleOrNull()?.let { it ->
                    User(
                        id = it[UsersTable.id],
                        name = it[UsersTable.name],
                        email = it[UsersTable.email]
                    )

                }
        }
        return user
    }
}