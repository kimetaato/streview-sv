package com.streview.infrastructure.api.stores

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.fold
import com.github.michaelbull.result.runCatching
import com.streview.domain.commons.GeoLocation
import com.streview.domain.commons.errors.DomainError
import com.streview.domain.commons.errors.TechnicalError
import com.streview.infrastructure.api.stores.dto.Circle
import com.streview.infrastructure.api.stores.dto.GooglePlace
import com.streview.infrastructure.api.stores.dto.GooglePlacesRequest
import com.streview.infrastructure.api.stores.dto.GooglePlacesResponse
import com.streview.infrastructure.api.stores.dto.Location
import com.streview.infrastructure.api.stores.dto.LocationBias
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody

class GooglePlacesDataSource(
    private val httpClient: HttpClient
) {
    companion object {
        private const val SERVICE_NAME = "Google Places API"
        private const val GOOGLE_API_KEY_ENV_NAME = "GOOGLE_API_KEY"

        private const val PLACES_API_URL = "https://places.googleapis.com/v1/places:searchText"
        private const val FIELD_MASK = "places.displayName,places.location,places.nationalPhoneNumber,places.websiteUri"
        private val apiKey = System.getenv(GOOGLE_API_KEY_ENV_NAME)!!
    }

    private fun createBody(geoLocation: GeoLocation, storeName: String): GooglePlacesRequest = GooglePlacesRequest(
        includedType = "restaurant",
        languageCode = "ja",
        locationBias = LocationBias(
            circle = Circle(
                center = Location(
                    latitude = geoLocation.latitude,
                    longitude = geoLocation.longitude
                ),
                radius = 500.0
            ),
        ),
        textQuery = storeName,
    )

    suspend fun searchText(geoLocation: GeoLocation, storeName: String): Result<GooglePlace, DomainError> =
        runCatching {
            val response = httpClient.post(PLACES_API_URL) {
                // ヘッダー設定
                header("Content-Type", "application/json")
                header("Accept", "application/json")
                header("X-Goog-Api-Key", apiKey)
                header("X-Goog-FieldMask", FIELD_MASK)
                header("Accept", "application/json")
                header("Content-Type", "application/json")

                setBody(createBody(geoLocation, storeName))
            }

            check(response.status.value == 200)
            response.body<GooglePlacesResponse>().places.first()
        }.fold(
            success = { googlePlace -> Ok(googlePlace) },
            failure = { throwable ->
                Err(
                    TechnicalError.ExternalServiceError(
                        serviceName = SERVICE_NAME,
                        cause = throwable,
                    )
                )
            }
        )
}
