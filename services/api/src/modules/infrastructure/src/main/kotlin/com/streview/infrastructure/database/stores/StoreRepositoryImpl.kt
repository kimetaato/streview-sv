package com.streview.infrastructure.database.stores

import com.streview.domain.stores.Store
import com.streview.domain.stores.StoreRepository
import com.streview.infrastructure.database.models.StoresTable
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.singleOrNull
import org.jetbrains.exposed.v1.r2dbc.select
import org.jetbrains.exposed.v1.r2dbc.selectAll
import org.jetbrains.exposed.v1.r2dbc.transactions.suspendTransaction

class StoreRepositoryImpl : StoreRepository {
    override suspend fun findById(id: String): Store? {
        val store: Store? = suspendTransaction {
            StoresTable
                .select(
                    StoresTable.id,
                    StoresTable.name,
                    StoresTable.address,
                    StoresTable.denwaBango,
                    StoresTable.description,
                    StoresTable.openingTime,
                    StoresTable.createdAt,
                    StoresTable.updatedAt,
                    StoresTable.deletedAt,
                    StoresTable.starCache,
                    StoresTable.latitude,
                    StoresTable.longitude,
                )
                .where { StoresTable.id eq id }
                .singleOrNull()?.let { toDomain(it) }
        }
        return store
    }

    override suspend fun getAllTest(): Flow<Store> {
        return suspendTransaction { StoresTable.selectAll().map { toDomain(it) } }
    }
}

//package com.streview.infrastructure.database.stores
//
//import com.streview.domain.stores.Store
//import com.streview.domain.stores.StoreRepository
//import com.streview.infrastructure.database.models.StoresTable
//import com.streview.infrastructure.database.models.UsersTable
//import kotlinx.coroutines.flow.singleOrNull
//import org.jetbrains.exposed.v1.r2dbc.select
//import org.jetbrains.exposed.v1.r2dbc.transactions.suspendTransaction
//
//class StoreRepositoryImpl: StoreRepository {
//    override suspend fun findById(id: String): Store? {
//        val store: Store? = suspendTransaction {
//            StoresTable
//                .select(
//                    StoresTable.id,
//                    StoresTable.name,
//                    StoresTable.address,
//                    StoresTable.denwaBango,
//                    StoresTable.description,
//                    StoresTable.openingTime,
//                    StoresTable.createdAt,
//                    StoresTable.updatedAt,
//                    StoresTable.deletedAt,
//                    StoresTable.starCache,
//                    StoresTable.latitude,
//                    StoresTable.longitude,
//                )
//                .where { StoresTable.id eq id }
//                .singleOrNull()?.let {  }
//        }
//    }
//
//
//}