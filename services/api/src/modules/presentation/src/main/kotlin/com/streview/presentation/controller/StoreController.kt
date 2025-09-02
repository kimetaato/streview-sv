package com.streview.presentation.controller

import com.streview.application.usecases.stores.GetGeofenceUseCase
import com.streview.application.usecases.visits.CheckInUseCase
import com.streview.application.usecases.visits.VisitStatusUseCase
import com.streview.common.dto.stores.CheckInRequest
import com.streview.common.dto.stores.GetGeofenceRequest
import com.streview.common.dto.stores.GetGeofenceRequestJson
import com.streview.common.dto.stores.Location
import com.streview.common.dto.stores.VisitStatusRequest
import com.streview.common.dto.stores.VisitStatusRequestJson
import io.ktor.http.HttpStatusCode
import io.ktor.server.auth.UserIdPrincipal
import io.ktor.server.auth.principal
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.post
import io.ktor.server.routing.put
import io.ktor.server.routing.route
import kotlinx.serialization.Serializable
import org.koin.ktor.ext.inject

fun Route.storeController() {
    val getGeofenceUseCase: GetGeofenceUseCase by inject()
    val checkInUseCase: CheckInUseCase by inject()
    val visitStatusUseCase: VisitStatusUseCase by inject()

    route("/stores") {
        post("/geofence") {
            val userID = call.principal<UserIdPrincipal>()!!.name
            val request = call.receive<GetGeofenceRequestJson>()

            val input = GetGeofenceRequest(
                userId = userID,
                location = Location(
                    request.location.lat,
                    request.location.lng
                ),
            )

            call.respond(HttpStatusCode.OK, getGeofenceUseCase.execute(input))
        }

        /**
         * 飲食店とそれに付随するレビューの情報を取得する。
         * 取得条件を設定することが可能
         */
        post("/") {
            @Serializable
            data class RequestJson(
                val sortBy: String? = null,
                val location: Location? = null,
            )
        }
        /**
         * 対象の飲食店に来店する
         */
        put("/{store_uuid}/check-in") {
            val userID = call.principal<UserIdPrincipal>()!!.name
            val storeUUID = call.parameters["store_uuid"]!!

            val input = CheckInRequest(
                userID = userID,
                storeUUID = storeUUID,
            )
            call.respond(HttpStatusCode.OK, checkInUseCase.execute(input))
        }

        put("/{store_uuid}") {
            val userID = call.principal<UserIdPrincipal>()!!.name
            val storeUUID = call.parameters["store_uuid"]!!
            val request = call.receive<VisitStatusRequestJson>()

            val input = VisitStatusRequest(
                userID = userID,
                storeUUID = storeUUID,
                status = request.status
            )
            call.respond(HttpStatusCode.OK, visitStatusUseCase.execute(input))
        }
    }
}
