package com.streview.infrastructure.database.stores

import com.streview.domain.stores.Store
import org.jetbrains.exposed.v1.core.ResultRow
import org.jetbrains.exposed.v1.core.statements.UpdateBuilder

fun toDomain(row: ResultRow): Store =
    Store.reconstruct(
        storeUUID = row[StoreTable.storeUUID],
        name = row[StoreTable.name],
        genre = row[StoreTable.genre],
        address = row[StoreTable.address],
        tel = row[StoreTable.phoneNumber],
        description = row[StoreTable.description],
        open = row[StoreTable.openingTime],
        latitude = row[StoreTable.latitude],
        longitude = row[StoreTable.longitude]
    )

fun toTable(store: Store): (UpdateBuilder<*>) -> Unit {
    return {
        with(StoreTable) {
            it[storeUUID] = store.storeUUID.value
            it[name] = store.name.value
            it[genre] = store.genre.value
            it[address] = store.address.value
            it[phoneNumber] = store.tel.value
            it[description] = store.description.value
            it[openingTime] = store.open.value
            it[latitude] = store.geoLocation.latitude
            it[longitude] = store.geoLocation.longitude
        }
    }
}
