package com.streview.presentation.controller

import com.streview.application.usecases.stores.TryStoreUseCase
import com.streview.application.usecases.stores.dto.TryStoreRequest
import com.streview.application.usecases.stores.dto.TryStoreResponse
import io.ktor.http.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Route.storeController() {
    val useCase: TryStoreUseCase by inject()
    val path = "/stores/check/health"
    get(path) {
        println("GET $path")
        val req = TryStoreRequest("uuid")
        val res: TryStoreResponse = useCase.execute(req)
        call.respond(HttpStatusCode.OK, res)
    }
}
