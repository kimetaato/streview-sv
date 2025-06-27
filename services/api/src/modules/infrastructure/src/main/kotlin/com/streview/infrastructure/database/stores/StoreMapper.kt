package com.streview.infrastructure.database.stores

import com.streview.domain.stores.Store
import com.streview.infrastructure.database.models.StoresTable
import org.jetbrains.exposed.v1.core.ResultRow
import org.jetbrains.exposed.v1.core.statements.UpdateBuilder

fun StoreRepositoryImpl.toDomain(result: ResultRow): Store {
    return Store.reconstruct(
        result[StoresTable.id],
        result[StoresTable.name],
    )
}

fun StoreRepositoryImpl.toTable(store: Store): (UpdateBuilder<*>) -> Unit {
    return {
        with(StoresTable) {
            it[id] = store.id.value
            it[name] = store.name.value
        }
    }
}