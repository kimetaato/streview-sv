package com.streview.presentation.controller

import com.streview.usecase.stores.TryStoreUseCase
import com.streview.usecase.stores.dto.TryStoreRequest
import com.streview.usecase.stores.dto.TryStoreResponse
import io.ktor.http.HttpStatusCode
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import org.koin.ktor.ext.inject

fun Route.storeController(){
    val useCase: TryStoreUseCase by inject()
    val path: String = "/stores/check/health"
    get(path){
        println("GET $path")
        val req = TryStoreRequest("uuid")
        val res: TryStoreResponse = useCase.execute(req)
        call.respond(HttpStatusCode.OK, res)
    }
}