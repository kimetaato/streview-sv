package com.streview.infrastructure.api.stores

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.mapError
import com.github.michaelbull.result.runCatching
import com.streview.domain.commons.GeoLocation
import com.streview.domain.commons.errors.DomainError
import com.streview.domain.commons.errors.TechnicalError
import com.streview.infrastructure.api.stores.dto.HotPepperResponse
import com.streview.infrastructure.api.stores.dto.HotPepperResult
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter

class HotPepperDataSource(
    private val httpClient: HttpClient
) {
    companion object {
        private const val SERVICE_NAME = "HotPepper API"
        private const val HOTPEPPER_API_KEY_ENV_NAME = "HOT_PEPPER_API_KEY"
        private const val HOTPEPPER_API_URL = "https://webservice.recruit.co.jp/hotpepper/gourmet/v1/"
    }

    fun getApiKey(): String {
        val apiKey = System.getenv(HOTPEPPER_API_KEY_ENV_NAME)
        check(!apiKey.isNullOrBlank()) { "Hotpepper API Key is not set or empty" }
        return apiKey
    }

    suspend fun searchNearBy(geoLocation: GeoLocation): Result<HotPepperResult, DomainError> =
        runCatching {
            val apiKey = getApiKey()
            val response = httpClient.get(HOTPEPPER_API_URL) {
                parameter("key", apiKey)
                parameter("lat", geoLocation.latitude.toString())
                parameter("lng", geoLocation.longitude.toString())
                parameter("range", "2")
                parameter("count", "20")
                parameter("format", "json")
            }
            response.body<HotPepperResponse>().results
        }.mapError { exception ->
            TechnicalError.ExternalServiceError(
                serviceName = SERVICE_NAME,
                cause = exception,
            )
        }
}
