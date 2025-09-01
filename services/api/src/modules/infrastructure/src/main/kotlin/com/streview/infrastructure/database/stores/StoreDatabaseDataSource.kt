package com.streview.infrastructure.database.stores

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.fold
import com.github.michaelbull.result.map
import com.github.michaelbull.result.runCatching
import com.streview.domain.commons.GeoLocation
import com.streview.domain.commons.UUID
import com.streview.domain.commons.errors.DomainError
import com.streview.domain.commons.errors.TechnicalError
import com.streview.domain.stores.Store
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import org.jetbrains.exposed.v1.core.SqlExpressionBuilder.inList
import org.jetbrains.exposed.v1.r2dbc.insert
import org.jetbrains.exposed.v1.r2dbc.selectAll
import kotlin.math.*

class StoreDatabaseDataSource {
    suspend fun findByUUID(uuid: UUID): Result<Store?, DomainError> =
        runCatching {
            StoreTable
                .selectAll()
                .where { StoreTable.storeUUID eq uuid.value }
                .firstOrNull()?.let { row ->
                    toDomain(row)
                }
        }.fold(
            success = { store -> Ok(store) },
            failure = { throwable -> Err(TechnicalError.DatabaseError(false, throwable)) }
        )

    suspend fun findInUUIDs(uuids: List<UUID>): Result<List<Store>, DomainError> =
        runCatching {
            StoreTable
                .selectAll()
                .where(StoreTable.storeUUID inList uuids.map { it.value })
                .map { row ->
                    toDomain(row)
                }.toList()
        }.fold(
            success = { stores -> Ok(stores) },
            failure = { throwable -> Err(TechnicalError.DatabaseError(false, throwable)) }
        )

    suspend fun save(store: Store): Result<Store, DomainError> =
        runCatching {
            StoreTable.insert { statement ->
                toTable(store)(statement)
            }
        }.fold(
            success = { Ok(store) },
            failure = { throwable -> Err(TechnicalError.DatabaseError(false, throwable)) }
        )

    suspend fun sortByDistanceInStoreUUIDs(
        storeUUIDs: List<UUID>,
        geoLocation: GeoLocation
    ): Result<List<Store>, DomainError> =
        runCatching {
            StoreTable
                .selectAll()
                .where { StoreTable.storeUUID inList storeUUIDs.map { it.value } }
                .map { row ->
                    toDomain(row)
                }.toList()
        }.map { stores ->
            // haversine関数で計測した距離を元にソート
            stores
                .sortedBy { store -> haversine(geoLocation, store) }
                .take(19)
        }.fold(
            success = { stores -> Ok(stores) },
            failure = { throwable -> Err(TechnicalError.DatabaseError(false, throwable)) }
        )
}

private fun haversine(geoLocation: GeoLocation, store: Store): Double {
    val r = 6372.8 // 地球の半径 (km)

    val dLat = Math.toRadians(store.geoLocation.latitude - geoLocation.latitude)
    val dLon = Math.toRadians(store.geoLocation.longitude - geoLocation.longitude)
    val a = sin(dLat / 2)
        .pow(2) + (
        sin(dLon / 2)
            .pow(2) * cos(Math.toRadians(geoLocation.latitude)) * cos(Math.toRadians(store.geoLocation.latitude))
        )
    val c = 2 * asin(sqrt(a))

    return r * c
}
