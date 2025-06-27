package com.streview.domain.stores

import kotlinx.coroutines.flow.Flow

interface StoreRepository{
    suspend fun findById(id: String): Store?
    suspend fun getAllTest(): Flow<Store>
}