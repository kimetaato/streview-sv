package com.streview.infrastructure.database.users

import com.streview.domain.users.User
import com.streview.infrastructure.database.models.UsersTable
import org.jetbrains.exposed.v1.core.Column
import org.jetbrains.exposed.v1.core.ResultRow
import org.jetbrains.exposed.v1.core.statements.UpdateBuilder
import org.jetbrains.exposed.v1.core.statements.UpdateStatement

fun UserRepositoryImpl.toDomain(result: ResultRow): User {
    return User.create(result[UsersTable.id], result[UsersTable.name], result[UsersTable.email])
}

fun UserRepositoryImpl.toUserTable(user: User): (UpdateBuilder<*>) -> Unit {
        return {
                with(UsersTable) {
                    it[id] = user.id.value
                    it[name] = user.name.value
                    it[email] = user.email.value
                }
        }
    }
