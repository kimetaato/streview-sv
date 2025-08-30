package com.streview.application.services

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.andThen
import com.streview.domain.commons.GeoLocation
import com.streview.domain.commons.UserID
import com.streview.domain.commons.errors.DomainError
import com.streview.domain.relays.RelayRepository
import com.streview.domain.reviews.ReviewRepository
import com.streview.domain.stores.Store
import com.streview.domain.stores.StoreRepository

class GetStoreService(
    private val storeRepository: StoreRepository,
    private val reviewRepository: ReviewRepository,
    private val relayRepository: RelayRepository
) {
    /**
     * Geofence設置用
     */
    suspend fun findNearByStore(userID: UserID, geoLocation: GeoLocation): Result<List<Store>, DomainError> {
        return relayRepository.findAllByUserId(userID)
            .andThen { relays ->
                reviewRepository.findInUUIDs(relays.map { it.reviewUUID })
            }.andThen { reviews ->
                storeRepository.sortByDistanceInUUIDs(reviews.map { it.storeUUID }, geoLocation)
            }
    }

    suspend fun find
}
