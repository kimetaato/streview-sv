package com.streview.configure

import com.streview.domain.exceptions.NotFoundException
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*

fun Application.configureStatusPage() {
    install(StatusPages) {

        // 例外に対するレスポンスを定義する
        exception<NotFoundException> { call, _ ->
            return@exception call.respond(HttpStatusCode.NotFound)
        }

        // 独自定義していない例外が発生した場合
        exception<Throwable> { call, _ ->
            return@exception call.response.status(HttpStatusCode.InternalServerError)
        }
    }
}