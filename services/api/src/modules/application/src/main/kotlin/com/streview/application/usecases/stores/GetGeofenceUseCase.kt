package com.streview.application.usecases.stores

import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.andThen
import com.github.michaelbull.result.fold
import com.streview.application.usecases.stores.dto.GetGeofenceRequest
import com.streview.application.usecases.stores.dto.GetGeofenceResponse
import com.streview.application.usecases.stores.dto.StoreHeader
import com.streview.domain.commons.GeoLocation
import com.streview.domain.commons.UserID
import com.streview.domain.stores.Store
import com.streview.domain.stores.StoreRepository
import com.streview.domain.visits.VisitRepository
import org.jetbrains.exposed.v1.r2dbc.transactions.suspendTransaction
import kotlin.math.asin
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

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
                }.andThen { stores ->
                    val range = haversineMeter(geoLocation, stores.last())
                    Ok(Pair(stores, range))
                }
        }.fold(
            success = { (stores, range) ->
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
                    range = range
                )
            },
            failure = {
                throw it
            }
        )

    private fun haversineMeter(geoLocation: GeoLocation, store: Store): Int {
        val r = 6372.8 // 地球の半径 (km)

        val dLat = Math.toRadians(store.geoLocation.latitude - geoLocation.latitude)
        val dLon = Math.toRadians(store.geoLocation.longitude - geoLocation.longitude)
        val a = sin(dLat / 2)
            .pow(2) + (
            sin(dLon / 2)
                .pow(2) * cos(Math.toRadians(geoLocation.latitude)) * cos(Math.toRadians(store.geoLocation.latitude))
            )
        val c = 2 * asin(sqrt(a))

        return (r * c * 1000).toInt()
    }
}
