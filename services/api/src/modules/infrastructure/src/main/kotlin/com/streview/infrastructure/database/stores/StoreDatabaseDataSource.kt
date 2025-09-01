package com.streview.infrastructure.database.stores

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import com.streview.domain.commons.GeoLocation
import com.streview.domain.commons.UUID
import com.streview.domain.commons.errors.DomainError
import com.streview.domain.commons.errors.TechnicalError
import com.streview.domain.stores.Store
import com.streview.infrastructure.database.models.StoresTable
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.exposed.v1.core.SqlExpressionBuilder.inList
import org.jetbrains.exposed.v1.r2dbc.insert
import org.jetbrains.exposed.v1.r2dbc.select
import org.jetbrains.exposed.v1.r2dbc.selectAll
import kotlin.math.*

class StoreDatabaseDataSource {
    suspend fun findByUUID(uuid: UUID): Result<Store?, DomainError> =
        try {
            Ok(
                StoresTable
                    .selectAll()
                    .where { StoresTable.storeUUID eq uuid.value }
                    .firstOrNull()?.let { row ->
                        Store.reconstruct(
                            storeUUID = row[StoresTable.storeUUID],
                            name = row[StoresTable.name],
                            genre = row[StoresTable.genre],
                            address = row[StoresTable.address],
                            tel = row[StoresTable.phoneNumber],
                            description = row[StoresTable.description],
                            open = row[StoresTable.openingTime],
                            latitude = row[StoresTable.latitude],
                            longitude = row[StoresTable.longitude]
                        )
                    }
            )
        } catch (e: Exception) {
            Err(TechnicalError.DatabaseError(false, e))
        }

    suspend fun findInUUIDs(uuids: List<UUID>): Result<List<Store>, DomainError> =
        try {
            Ok(
                StoresTable.select(
                    StoresTable.storeUUID,
                    StoresTable.name,
                    StoresTable.genre,
                    StoresTable.address,
                    StoresTable.phoneNumber,
                    StoresTable.description,
                    StoresTable.openingTime,
                    StoresTable.latitude,
                    StoresTable.longitude
                )
                    .where(StoresTable.storeUUID inList uuids.map { it.value })
                    .toList().map { row ->
                        Store.reconstruct(
                            storeUUID = row[StoresTable.storeUUID],
                            name = row[StoresTable.name],
                            genre = row[StoresTable.genre],
                            address = row[StoresTable.address],
                            tel = row[StoresTable.phoneNumber],
                            description = row[StoresTable.description],
                            open = row[StoresTable.openingTime],
                            latitude = row[StoresTable.latitude],
                            longitude = row[StoresTable.longitude]
                        )
                    }
            )
        } catch (e: Exception) {
            Err(TechnicalError.DatabaseError(false, e))
        }

    suspend fun save(store: Store): Result<Store, DomainError> {
        return try {
            StoresTable.insert {
                it[storeUUID] = store.storeUUID.value
                it[name] = store.name.value
                it[genre] = store.genre.value
                it[address] = store.address.value
                it[phoneNumber] = store.tel.value
                it[description] = store.description.value
                it[openingTime] = store.open.value
                it[latitude] = store.geoLocation.latitude
                it[longitude] = store.geoLocation.longitude
                it[createdAt] = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
                it[updatedAt] = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
                it[deletedAt] = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
                it[starCache] = 0.0
            }
            Ok(store)
        } catch (e: Exception) {
            Err(TechnicalError.DatabaseError(false, e))
        }
    }

    suspend fun sortByDistanceInUUIDs(uuids: List<UUID>, geoLocation: GeoLocation): Result<List<Store>, DomainError> {
        return try {
            val stores = StoresTable
                .selectAll()
                .where { StoresTable.storeUUID inList uuids.map { it.value } }
                .map { row ->
                    Store.reconstruct(
                        storeUUID = row[StoresTable.storeUUID],
                        name = row[StoresTable.name],
                        genre = row[StoresTable.genre],
                        address = row[StoresTable.address],
                        tel = row[StoresTable.phoneNumber],
                        description = row[StoresTable.description],
                        open = row[StoresTable.openingTime],
                        latitude = row[StoresTable.latitude],
                        longitude = row[StoresTable.longitude]
                    )
                }.toList()
            println(stores)

            // 距離を計算し、元の店舗情報と一緒に新しいリストを作成
            val storesWithDistance = stores.map { store ->
                val distance = haversine(geoLocation, store)
                store to distance // Pair<Store, Double> を作成
            }

            // Pair<Store, Double> のリストを距離でソートし、上位19件を取得
            val nearbyStores = storesWithDistance
                .sortedBy { it.second } // Pairの2番目の要素（距離）でソート
                .take(19)
                .map { it.first } // ソート後、元のStoreオブジェクトを抽出

            return Ok(nearbyStores)
        } catch (e: Exception) {
            Err(TechnicalError.DatabaseError(false, e))
        }
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
}
