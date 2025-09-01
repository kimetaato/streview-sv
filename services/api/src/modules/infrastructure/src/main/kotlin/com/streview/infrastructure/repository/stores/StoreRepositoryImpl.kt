package com.streview.infrastructure.repository.stores

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import com.github.michaelbull.result.fold
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
    override suspend fun findByUUID(uuid: UUID): Result<Store?, DomainError> = db.findByUUID(uuid)

    override suspend fun findInUUIDs(uuids: List<UUID>): Result<List<Store>, DomainError> = db.findInUUIDs(uuids)

    override suspend fun save(store: Store): Result<Store, DomainError> = db.save(store)

    override suspend fun sortByDistanceInUUIDs(
        uuids: List<UUID>,
        geoLocation: GeoLocation
    ): Result<List<Store>, DomainError> = db.sortByDistanceInUUIDs(uuids, geoLocation)

    override suspend fun searchFromGeoLocation(geoLocation: GeoLocation): Result<List<Store>, DomainError> {
        val stores = mutableListOf<Store>()

        hotPepperDataSource.searchNearBy(geoLocation).fold(
            success = { hotPepperShop ->
                println("DEBUG: ${hotPepperShop.shop.size} shop found")
                hotPepperShop.shop.forEach { shop ->
                    googlePlacesDataSource.searchText(geoLocation, shop.name).fold(
                        success = {
                            println("DEBUG: ${it.location} in ${shop.name} found.")
                            createMergedStore(
                                hotpepperShop = shop,
                                googlePlace = it
                            )?.let { store -> stores.add(store) }
                        },
                        failure = {
                            print(it.stackTraceToString())
                        }
                    )
                }
            },
            failure = {
                print(it.stackTraceToString())
            }
        )

        return binding { stores.toList() }
    }

    private fun createMergedStore(hotpepperShop: HotPepperShop, googlePlace: GooglePlace): Store? {
        val store = Store.create(
            name = hotpepperShop.name,
            genre = hotpepperShop.genre.name,
            address = hotpepperShop.address,
            tel = googlePlace.phoneNumber,
            description = hotpepperShop.catch,
            open = hotpepperShop.open,
            latitude = googlePlace.location.latitude,
            longitude = googlePlace.location.longitude
        ).fold(
            success = { store -> store },
            failure = {
                null
            }
        )

        return store
    }
}
