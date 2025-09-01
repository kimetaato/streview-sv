package com.streview.application.usecases.stores

import com.github.michaelbull.result.andThen
import com.github.michaelbull.result.fold
import com.streview.application.usecases.stores.dto.GetGeofenceRequest
import com.streview.application.usecases.stores.dto.GetGeofenceResponse
import com.streview.application.usecases.stores.dto.StoreHeader
import com.streview.domain.commons.GeoLocation
import com.streview.domain.commons.UserID
import com.streview.domain.stores.StoreRepository
import com.streview.domain.visits.VisitRepository
import org.jetbrains.exposed.v1.r2dbc.transactions.suspendTransaction

class GetGeofenceUseCase(
    private val visitRepository: VisitRepository,
    private val storeRepository: StoreRepository,
) {
    suspend fun execute(input: GetGeofenceRequest): GetGeofenceResponse =
        suspendTransaction {
            val userID = UserID(input.userId)

            val geoLocation = GeoLocation.create(
                latitude = input.location.lat,
                longitude = input.location.lng,
            ).value

            /**
             * Geofence設置用
             * visitのstatusがwant　→　行きたい店の
             * 中から距離が近い順に19件取得する
             */
            visitRepository.findByUserIDAndWant(userID)
                .andThen { visits ->
                    storeRepository.sortByDistanceInUUIDs(visits.map { it.storeUUID }, geoLocation)
                }
        }.fold(
            success = { stores ->
                val storeHeaders = stores.map { store ->
                    StoreHeader(
                        uuid = store.storeUUID.value,
                        name = store.name.value,
                        lat = store.geoLocation.latitude,
                        lng = store.geoLocation.longitude
                    )
                }
                GetGeofenceResponse(
                    stores = storeHeaders,
                    range = 100.0
                )
            },
            failure = {
                throw it
            }
        )
}
