package com.streview.presentation.controller

import com.streview.application.usecases.encounters.EncounterUseCase
import com.streview.common.dto.encounters.EncounterRequest
import com.streview.common.dto.encounters.EncounterRequestJson
import io.ktor.http.HttpStatusCode
import io.ktor.server.auth.UserIdPrincipal
import io.ktor.server.auth.principal
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.post
import org.koin.ktor.ext.inject

fun Route.encounterController() {
    val encounterUseCase: EncounterUseCase by inject()

    post("/encounters") {
        // ユーザー取得
        val userID = call.principal<UserIdPrincipal>()!!.name

        // json取得
        val requestJson = call.receive<EncounterRequestJson>()

        val req = EncounterRequest(
            userID,
            requestJson.encounters
        )

        val res = encounterUseCase.execute(req)
        call.respond(HttpStatusCode.OK, res)
    }
}
