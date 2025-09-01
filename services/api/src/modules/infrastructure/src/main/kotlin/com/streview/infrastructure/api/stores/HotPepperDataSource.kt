package com.streview.infrastructure.api.stores

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.fold
import com.github.michaelbull.result.runCatching
import com.streview.domain.commons.GeoLocation
import com.streview.domain.commons.errors.DomainError
import com.streview.domain.commons.errors.TechnicalError
import com.streview.infrastructure.api.stores.dto.HotPepperResponse
import com.streview.infrastructure.api.stores.dto.HotPepperShop
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter

class HotPepperDataSource(
    private val httpClient: HttpClient
) {
    companion object {
        private const val SERVICE_NAME = "HotPepper API"
        private const val HOT_PEPPER_API_KEY_ENV_NAME = "HOT_PEPPER_API_KEY"
        private const val HOT_PEPPER_API_URL = "https://webservice.recruit.co.jp/hotpepper/gourmet/v1/"
        private val apiKey = System.getenv(HOT_PEPPER_API_KEY_ENV_NAME)!!
    }

    suspend fun searchNearBy(geoLocation: GeoLocation): Result<List<HotPepperShop>, DomainError> =
        runCatching {
            val response = httpClient.get(HOT_PEPPER_API_URL) {
                parameter("key", apiKey)
                parameter("lat", geoLocation.latitude.toString())
                parameter("lng", geoLocation.longitude.toString())
                parameter("range", "2")
                parameter("count", "20")
                parameter("format", "json")
            }
            response.body<HotPepperResponse>().results.shop
        }.fold(
            success = { hotPepperShops -> Ok(hotPepperShops) },
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
