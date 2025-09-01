package com.streview.presentation.controller

import com.streview.application.usecases.relays.MarkRelayStatusUseCase
import com.streview.application.usecases.relays.dto.RelayStatusToggleRequest
import com.streview.application.usecases.reviews.GetMyReviewUseCase
import com.streview.application.usecases.reviews.GetReReviewsUseCase
import com.streview.application.usecases.reviews.PostReviewUseCase
import com.streview.application.usecases.reviews.dto.GetMyReviewsRequest
import com.streview.application.usecases.reviews.dto.GetReReviewRequest
import com.streview.application.usecases.reviews.dto.PostReviewRequest
import com.streview.domain.commons.errors.ValidationError
import com.streview.domain.exceptions.InvalidInputException
import io.ktor.http.HttpStatusCode
import io.ktor.http.content.PartData
import io.ktor.http.content.forEachPart
import io.ktor.server.auth.UserIdPrincipal
import io.ktor.server.auth.principal
import io.ktor.server.request.receive
import io.ktor.server.request.receiveMultipart
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.patch
import io.ktor.server.routing.post
import io.ktor.server.routing.put
import io.ktor.server.routing.route
import io.ktor.utils.io.readBuffer
import kotlinx.io.Source
import kotlinx.serialization.Serializable
import org.koin.ktor.ext.inject
import java.math.BigDecimal

@Suppress("LongMethod", "ThrowsCount")
fun Route.reviewController() {
    /**
     * レビューを投稿する
     */
    val postReviewUseCase: PostReviewUseCase by inject()
    val getMyPostedReviewsUseCase: GetMyReviewUseCase by inject()
    val getReReviewsUseCase: GetReReviewsUseCase by inject()
    val markUseCase: MarkRelayStatusUseCase by inject()

    route("/reviews") {
        post("/") {
            val userID = call.principal<UserIdPrincipal>()!!.name
            val multiplatform = call.receiveMultipart() // デフォルトで50MBまでの受信に対応している

            val imageSource = mutableListOf<Source>()
            var storeUUID: String? = null
            var comment: String? = null
            var star: BigDecimal? = null
            multiplatform.forEachPart { part ->
                when (part) {
                    // ファイルとして送信されたものを受け取る
                    is PartData.FileItem -> {
                        imageSource.add(part.provider().readBuffer())
                    }

                    is PartData.FormItem -> when (part.name) {
                        "storeUUID" -> storeUUID = part.value
                        "comment" -> comment = part.value
                        "star" -> star = part.value.toBigDecimal()
                    }

                    else -> Unit
                }
                part.dispose()
            }
            if (imageSource.isEmpty()) throw InvalidInputException("image")
            if (storeUUID.isNullOrBlank() || comment.isNullOrBlank() || star == null) {
                throw ValidationError.Required("field")
            }
            val input = PostReviewRequest(
                userID = userID,
                storeUUID = storeUUID,
                comment = comment,
                star = star,
                imageSources = imageSource,
            )

            call.respond(HttpStatusCode.Created, postReviewUseCase.execute(input))
        }

        /**
         * 自身の投稿したレビューの一覧を取得する
         */
        get("/posted") {
            val userID = call.principal<UserIdPrincipal>()!!.name

            val input = GetMyReviewsRequest(
                userID = userID,
            )

            call.respond(HttpStatusCode.OK, getMyPostedReviewsUseCase.execute(input))
        }

        get("/re-review") {
            val userID = call.principal<UserIdPrincipal>()!!.name

            val input = GetReReviewRequest(
                userID = userID,
            )

            call.respond(HttpStatusCode.OK, getReReviewsUseCase.execute(input))
        }

        patch("/{reviewUUID}") {
            // JSONバインド用クラス
            @Serializable
            data class RelayStatusToggleJson(
                val status: String,
            )

            // userID取得
            val userID = call.principal<UserIdPrincipal>()!!.name

            val reviewUUID = call.parameters["reviewUUID"]!!

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

        /**
         * 自分の投稿したレビューを更新する
         * 公開非公開, 文章の追記
         */
        put("/{reviewUUID}") {
        }
    }
}
