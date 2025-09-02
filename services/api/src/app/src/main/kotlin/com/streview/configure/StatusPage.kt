package com.streview.configure

import com.streview.domain.commons.errors.DomainError
import com.streview.domain.commons.errors.EntityError
import com.streview.domain.commons.errors.TechnicalError
import com.streview.domain.commons.errors.ValidationError
import com.streview.domain.exceptions.ConflictException
import com.streview.domain.exceptions.InvalidInputException
import com.streview.domain.exceptions.NotFoundException
import com.streview.domain.exceptions.ValidationException
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*

fun Application.configureStatusPage() {
    install(StatusPages) {
        // 例外に対するレスポンスを定義する
        exception<NotFoundException> { call, cause ->
            println(cause.message)
            return@exception call.respond(HttpStatusCode.NotFound)
        }
        exception<InvalidInputException> { call, cause ->
            println(cause.message)
            return@exception call.respond(HttpStatusCode.BadRequest, cause.message ?: "")
        }
        // バリデーションに問題問題があった場合のエラーレスポンス
        exception<ValidationException> { call, cause ->
            println(cause.validationErrors)
            return@exception call.respond(HttpStatusCode.BadRequest, cause.validationErrors)
        }
        exception<ConflictException> { call, cause ->
            println(cause.message)
            return@exception call.respond(HttpStatusCode.Conflict, cause.message ?: "")
        }

        // 独自定義していない例外が発生した場合
        exception<Throwable> { call, cause ->
            return@exception (cause as? DomainError)?.let { domainError ->
                when (domainError) {
                    EntityError.AlreadyExist -> call.respond(HttpStatusCode.Conflict)
                    is EntityError.NotFound -> call.respond(HttpStatusCode.NotFound)
                    is TechnicalError.DatabaseError -> call.respond(HttpStatusCode.InternalServerError)
                    is TechnicalError.ExternalServiceError -> call.respond(HttpStatusCode.FailedDependency)
                    is TechnicalError.NetworkError -> call.respond(HttpStatusCode.ServiceUnavailable)
                    is ValidationError.InvalidFormat -> {
                        call.respond(HttpStatusCode.BadRequest, domainError.fieldName to domainError.rule.value)
                    }
                    is ValidationError.Multiple -> {
                        call.respond(HttpStatusCode.BadRequest, domainError.errors)
                    }
                    is ValidationError.Required -> {
                        call.respond(HttpStatusCode.BadRequest, domainError.fieldName)
                    }
                }
            } ?: call.response.status(HttpStatusCode.InternalServerError)
        }
    }
}
