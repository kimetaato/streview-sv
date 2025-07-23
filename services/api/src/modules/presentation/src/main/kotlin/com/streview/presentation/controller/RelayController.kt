package com.streview.presentation.controller

import com.streview.application.usecases.relays.MarkRelayStatusUseCase
import com.streview.application.usecases.relays.dto.RelayStatusToggleRequest
import com.streview.domain.exceptions.InvalidInputException
import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable
import org.koin.ktor.ext.inject

fun Route.relayController() {
    val markUseCase: MarkRelayStatusUseCase by inject()
    patch("/reviews/{r_id}") {
        // JSONバインド用クラス
        @Serializable
        data class RelayStatusToggleJson(
            val status: String,
        )

        // userID取得
        val userID = call.principal<UserIdPrincipal>()!!.name
        // パスパラメータ取得
        val reviewUUID = call.parameters["r_id"] ?: throw InvalidInputException(message = "操作対象がありません。")
        // ステータス取得
        val relayStatusToggleJson = call.receive<RelayStatusToggleJson>()

        val status = when (relayStatusToggleJson.status) {
            "public" -> {
                true
            }
            "private" -> {
                false
            }
            else -> {
                throw InvalidInputException("ステータスの値が不正です。")
            }
        }

        // DTOに値を詰める
        val relayStatusToggleRequest = RelayStatusToggleRequest(
            userID = userID,
            reviewUUID = reviewUUID,
            toggleStatus = status
        )

        val res = markUseCase.execute(relayStatusToggleRequest)
        call.respond(HttpStatusCode.OK, res)
    }
}
