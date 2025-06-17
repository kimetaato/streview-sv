package com.streview.presentation.controller


import com.streview.domain.exceptions.NotFoundException
import com.streview.usecase.users.GetUserUseCase
import com.streview.usecase.users.dto.UserRequest
import com.streview.usecase.users.dto.UserResponse
import io.ktor.http.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Route.userController() {
    val useCase: GetUserUseCase by inject()
    get("/users/{userId}") {
        println("GET /users")
        val userId = call.parameters["userId"]?.toIntOrNull()
            ?: throw NotFoundException("User id can not received")
        val req = UserRequest(userId)
        val response: UserResponse = useCase.execute(req)
        call.respond(HttpStatusCode.OK, response)
    }
}