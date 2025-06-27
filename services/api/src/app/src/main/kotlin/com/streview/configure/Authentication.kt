package com.streview.configure

import com.kborowy.authprovider.firebase.firebase
import io.ktor.server.application.*
import io.ktor.server.auth.*


fun Application.configureAuthentication() {
    install(Authentication) {
        val firebaseAdminResource = Thread.currentThread().contextClassLoader.getResourceAsStream("admin.json")

        firebase("firebase-auth") {
            adminInputStream = firebaseAdminResource
            validate { credentials ->
                UserIdPrincipal(credentials.uid)
            }
        }
    }
}