package com.streview.domain.users


interface IUserRepository {
    suspend fun findById(id: Int): User?
}

