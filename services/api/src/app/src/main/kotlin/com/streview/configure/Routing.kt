package com.streview.configure

import com.streview.presentation.controller.relayController
import com.streview.presentation.controller.userController
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.plugins.swagger.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureRouting() {
    routing {
        swaggerUI(
            path = "swagger",
            swaggerFile = "/openapi/openapi.yaml"
        ) // TODO: ユーザアカウントにrootみたいなロールを追加して、アクセス許可みたいなの必要かも？

        get("/") {
            println("GET / called")
            call.respondText("Server is running")
        }

        authenticate("firebase-auth") {
            userController()
            relayController()
        }
    }
}
