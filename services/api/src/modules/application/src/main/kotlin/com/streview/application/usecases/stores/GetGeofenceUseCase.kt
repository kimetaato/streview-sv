package com.streview.application.usecases.stores

import com.github.michaelbull.result.andThen
import com.github.michaelbull.result.fold
import com.streview.application.services.GetStoreService
import com.streview.application.usecases.stores.dto.GetGeofenceRequest
import com.streview.application.usecases.stores.dto.GetGeofenceResponse
import com.streview.application.usecases.stores.dto.StoreHeader
import com.streview.domain.commons.GeoLocation
import com.streview.domain.commons.UserID
import org.jetbrains.exposed.v1.r2dbc.transactions.suspendTransaction

class GetGeofenceUseCase(
    private val getStoreService: GetStoreService
) {
    suspend fun execute(input: GetGeofenceRequest): GetGeofenceResponse =
        suspendTransaction {
            val userID = UserID(input.userId)

            GeoLocation.create(
                latitude = input.location.lat,
                longitude = input.location.lng,
            ).andThen {
                getStoreService.searchNearByStore(userID, it)
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
}
