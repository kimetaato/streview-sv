package com.streview.infrastructure.repository.stores

import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.fold
import com.github.michaelbull.result.map
import com.github.michaelbull.result.onSuccess
import com.streview.domain.commons.GeoLocation
import com.streview.domain.commons.UUID
import com.streview.domain.commons.errors.DomainError
import com.streview.domain.stores.Store
import com.streview.domain.stores.StoreRepository
import com.streview.infrastructure.api.stores.GooglePlacesDataSource
import com.streview.infrastructure.api.stores.HotPepperDataSource
import com.streview.infrastructure.api.stores.dto.GooglePlace
import com.streview.infrastructure.api.stores.dto.HotPepperShop
import com.streview.infrastructure.database.stores.StoreDatabaseDataSource

class StoreRepositoryImpl(
    private val db: StoreDatabaseDataSource,
    private val hotPepperDataSource: HotPepperDataSource,
    private val googlePlacesDataSource: GooglePlacesDataSource,
) : StoreRepository {
    override suspend fun findByStoreUUID(storeUUID: UUID): Result<Store?, DomainError> = db.findByUUID(storeUUID)

    override suspend fun findInStoreUUIDs(storeUUIDs: List<UUID>): Result<List<Store>, DomainError> =
        db.findInUUIDs(storeUUIDs)

    override suspend fun save(store: Store): Result<Store, DomainError> = db.save(store)

    override suspend fun sortByDistanceInStoreUUIDs(
        storeUUIDs: List<UUID>,
        geoLocation: GeoLocation
    ): Result<List<Store>, DomainError> =
        db.sortByDistanceInStoreUUIDs(storeUUIDs, geoLocation)

    override suspend fun searchFromGeoLocation(geoLocation: GeoLocation): Result<List<Store>, DomainError> {
        val stores = mutableListOf<Store>()
        hotPepperDataSource.searchNearBy(geoLocation)
            .map { hotPepperShops ->
                hotPepperShops.map {
                    googlePlacesDataSource.searchText(geoLocation, it.name)
                        .onSuccess { googlePlace ->
                            createMergedStore(
                                hotPepperShop = it,
                                googlePlace = googlePlace
                            )?.let { store ->
                                stores.add(store)
                            }
                        }
                }
            }
        return Ok(stores)
    }

    private fun createMergedStore(hotPepperShop: HotPepperShop, googlePlace: GooglePlace): Store? =
        Store.create(
            name = hotPepperShop.name,
            genre = hotPepperShop.genre.name,
            address = hotPepperShop.address,
            tel = googlePlace.phoneNumber,
            description = hotPepperShop.catch,
            open = hotPepperShop.open,
            latitude = googlePlace.location.latitude,
            longitude = googlePlace.location.longitude
        ).fold(
            success = { store -> store },
            failure = { _ -> null }
        )
}
