package com.streview.presentation.controller

import com.streview.usecase.relays.GetPublicReviewsUseCase
import com.streview.usecase.relays.MarkReviewAsRereviewedUseCase
import io.ktor.http.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Route.relayController() {
    val markUseCase : MarkReviewAsRereviewedUseCase by inject()
    val getUseCase : GetPublicReviewsUseCase by inject()


}