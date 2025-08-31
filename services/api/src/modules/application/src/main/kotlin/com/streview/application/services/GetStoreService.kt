package com.streview.application.services

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.andThen
import com.github.michaelbull.result.map
import com.streview.domain.commons.GeoLocation
import com.streview.domain.commons.UserID
import com.streview.domain.commons.errors.DomainError
import com.streview.domain.reviews.Review
import com.streview.domain.reviews.ReviewRepository
import com.streview.domain.stores.Store
import com.streview.domain.stores.StoreRepository
import com.streview.domain.visits.VisitRepository

class GetStoreService(
    private val visitRepository: VisitRepository,
    private val storeRepository: StoreRepository,
    private val reviewRepository: ReviewRepository,
) {
    /**
     * Geofence設置用
     * visitのstatusがwant　→　行きたい店の
     * 中から距離が近い順に19件取得する
     */
    suspend fun searchNearByStore(userID: UserID, geoLocation: GeoLocation): Result<List<Store>, DomainError> {
        return visitRepository.findByUserIDAndWant(userID)
            .andThen { visits ->
                storeRepository.sortByDistanceInUUIDs(visits.map { it.storeUUID }, geoLocation)
            }
    }

    /**
     * ホーム画面の表示用
     * visitのstatusがNeutral → 初対面or前回興味なし
     * の飲食店の情報に自身が受け取ったレビューを付けて取得する
     */
    suspend fun getListStoreWithReview(userID: UserID): Result<Map<Store, List<Review>>, DomainError> {
        return visitRepository.findByUserIDAndNeutral(userID)
            .andThen { visits ->
                storeRepository.findInUUIDs(visits.map { it.storeUUID })
            }.andThen { stores ->
                reviewRepository.findByStoreUUIDs(stores.map { it.storeUUID })
                    .map {
                        stores.associateWith { store -> it.filter { review -> review.storeUUID == store.storeUUID } }
                    }
            }
    }
}
