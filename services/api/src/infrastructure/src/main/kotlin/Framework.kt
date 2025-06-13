package com.streview

import io.ktor.http.*
import io.ktor.server.application.*
import org.koin.ktor.plugin.Koin

fun Application.configureFramework() {
    install(Koin) {

    }
    println("Koin is installed")
}
