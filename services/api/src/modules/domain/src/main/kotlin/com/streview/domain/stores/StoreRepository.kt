package com.streview.domain.stores

import com.github.michaelbull.result.Result
import com.streview.domain.commons.GeoLocation
import com.streview.domain.commons.UUID
import com.streview.domain.commons.errors.DomainError

interface StoreRepository {
    suspend fun findByStoreUUID(storeUUID: UUID): Result<Store?, DomainError>
    suspend fun findInStoreUUIDs(storeUUIDs: List<UUID>): Result<List<Store>, DomainError>
    suspend fun save(store: Store): Result<Store, DomainError>
    suspend fun sortByDistanceInStoreUUIDs(
        storeUUIDs: List<UUID>,
        geoLocation: GeoLocation
    ): Result<List<Store>, DomainError>
    suspend fun searchFromGeoLocation(geoLocation: GeoLocation): Result<List<Store>, DomainError>
}
