package com.streview.domain.users

import com.github.michaelbull.result.Result
import com.streview.domain.commons.UserID
import com.streview.domain.commons.errors.DomainError

interface UserRepository {
    suspend fun findByID(userID: UserID): Result<User?, DomainError>
    suspend fun save(user: User): Result<User, DomainError>
    suspend fun updateProfile(user: User): Result<User, DomainError>
}
