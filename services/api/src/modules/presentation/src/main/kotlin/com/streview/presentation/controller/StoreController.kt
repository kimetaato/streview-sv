package com.streview.presentation.controller

import com.github.michaelbull.result.getOrThrow
import com.streview.application.stores.GetGeofenceUseCase
import com.streview.application.stores.dto.GetGeofenceRequest
import com.streview.application.stores.dto.Location
import io.ktor.http.HttpStatusCode
import io.ktor.server.auth.UserIdPrincipal
import io.ktor.server.auth.principal
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.post
import kotlinx.serialization.Serializable
import org.koin.ktor.ext.inject

fun Route.storeController() {
    val getGeofenceUseCase: GetGeofenceUseCase by inject()
    post("/stores/geofence") {
        val userID = call.principal<UserIdPrincipal>()!!.name

        @Serializable
        data class RequestJson(
            val location: Location,
        )

        val request = call.receive<RequestJson>()

        val input = GetGeofenceRequest(
            userId = userID,
            location = Location(request.location.lat, request.location.lng),
        )

        call.respond(HttpStatusCode.OK, getGeofenceUseCase.execute(input).getOrThrow())
    }

    post("/stores/search") {
        @Serializable
        data class RequestJson(
            val name: String? = null,
            val sortBy: String? = null,
            val location: Location? = null,
        )
    }
}
