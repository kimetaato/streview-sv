package com.streview.configure

import com.streview.domain.exceptions.ConflictException
import com.streview.domain.exceptions.NotFoundException
import com.streview.domain.exceptions.ValidationException
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
        // バリデーションに問題問題があった場合のエラーレスポンス
        exception<ValidationException> { call, cause ->
            return@exception call.respond(HttpStatusCode.BadRequest, cause.validationErrors)
        }
        exception<ConflictException> { call, cause ->
            return@exception call.respond(HttpStatusCode.Conflict, cause.message ?: "")
        }

        // 独自定義していない例外が発生した場合
        exception<Throwable> { call, _ ->
            return@exception call.response.status(HttpStatusCode.InternalServerError)
        }
    }
}