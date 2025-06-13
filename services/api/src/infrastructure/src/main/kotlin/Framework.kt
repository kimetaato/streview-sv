package com.streview

import io.ktor.server.application.*
import org.koin.dsl.module
import org.koin.ktor.plugin.Koin

fun Application.configureFramework() {
    install(Koin) {
        modules(module {


        })
    }
    println("Koin is installed")
}
