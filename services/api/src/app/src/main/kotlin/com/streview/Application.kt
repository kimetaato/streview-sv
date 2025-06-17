package com.streview


import com.streview.configure.*
import io.ktor.server.application.*
import io.ktor.server.netty.*


fun main(args: Array<String>) {
    EngineMain.main(args)
}

fun Application.module() {
    configureFramework()
    configureHTTP()
    configureDatabase()
    configureStatusPage()
    configureRouting()
    configureSerialization()
}