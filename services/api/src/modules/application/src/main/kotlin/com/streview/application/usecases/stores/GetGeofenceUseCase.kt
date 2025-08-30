package com.streview.application.usecases.stores

import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.andThen
import com.streview.application.services.GetStoreService
import com.streview.application.usecases.stores.dto.GetGeofenceRequest
import com.streview.application.usecases.stores.dto.GetGeofenceResponse
import com.streview.application.usecases.stores.dto.StoreHeader
import com.streview.domain.commons.GeoLocation
import com.streview.domain.commons.UserID
import com.streview.domain.commons.errors.DomainError

class GetGeofenceUseCase(
    private val getStoreService: GetStoreService
) {
    suspend fun execute(input: GetGeofenceRequest): Result<GetGeofenceResponse, DomainError> {
        val userID = UserID(input.userId)

        return GeoLocation.create(
            latitude = input.location.lat,
            longitude = input.location.lng,
        ).andThen {
            getStoreService.searchNearByStore(userID, it)
        }.andThen { stores ->
            val storeHeaders = stores.map { store ->
                StoreHeader(
                    uuid = store.storeUUID.value,
                    name = store.name.value,
                    lat = store.geoLocation.latitude,
                    lng = store.geoLocation.longitude
                )
            }
            Ok(
                GetGeofenceResponse(
                    stores = storeHeaders,
                    range = 100.0
                )
            )
        }
    }
}
