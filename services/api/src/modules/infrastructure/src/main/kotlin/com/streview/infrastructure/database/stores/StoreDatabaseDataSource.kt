package com.streview.infrastructure.database.stores

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import com.streview.domain.commons.UUID
import com.streview.domain.commons.errors.DomainError
import com.streview.domain.commons.errors.TechnicalError
import com.streview.domain.stores.Store
import com.streview.infrastructure.database.models.StoresTable
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.exposed.v1.r2dbc.insert
import org.jetbrains.exposed.v1.r2dbc.select

class StoreDatabaseDataSource {
    suspend fun findByUUID(uuid: UUID): Result<Store, DomainError> {
        try {
            val store = StoresTable
                .select(
                    StoresTable.id,
                    StoresTable.name,
                    StoresTable.genre,
                    StoresTable.address,
                    StoresTable.denwaBango,
                    StoresTable.description,
                    StoresTable.openingTime,
                    StoresTable.latitude,
                )
                .where { StoresTable.id eq uuid.value }
                .firstOrNull()?.let { row ->
                    Store.reconstruct(
                        storeUUID = row[StoresTable.id],
                        name = row[StoresTable.name],
                        genre = row[StoresTable.genre],
                        address = row[StoresTable.address],
                        tel = row[StoresTable.denwaBango],
                        description = row[StoresTable.description],
                        open = row[StoresTable.openingTime],
                        latitude = row[StoresTable.latitude],
                        longitude = row[StoresTable.longitude]
                    )
                }
            return if (store != null) {
                Ok(store)
            } else {
                Err(TechnicalError.DatabaseError(false, Exception("Store not found")))
            }
        } catch (e: Exception) {
            return Err(TechnicalError.DatabaseError(false, e))
        }
    }

    suspend fun save(store: Store): Result<Store, DomainError> {
        return try {
            StoresTable.insert {
                it[id] = store.storeUUID.value
                it[name] = store.name.value
                it[genre] = store.genre.value
                it[address] = store.address.value
                it[denwaBango] = store.tel.value
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
}
