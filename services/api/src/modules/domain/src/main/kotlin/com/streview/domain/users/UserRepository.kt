package com.streview.domain.users

import com.streview.domain.commons.UserID

interface IUserRepository {
    suspend fun findByID(userID: UserID): User?
    suspend fun create(user: User): User
    suspend fun updateProfile(user: User): User
}