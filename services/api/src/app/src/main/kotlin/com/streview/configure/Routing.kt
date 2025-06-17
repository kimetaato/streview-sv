package com.streview.configure

import com.streview.presentation.controller.userController
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*


fun Application.configureRouting() {
    routing {
        routing {
            get("/") {
                println("GET / called")
                call.respondText("Server is running")
            }

            userController()
        }
    }
}