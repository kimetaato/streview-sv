package com.streview.presentation.controller

import com.streview.application.usecases.reviews.GetMyReviewUseCase
import com.streview.application.usecases.reviews.PostReviewUseCase
import com.streview.application.usecases.reviews.dto.GetMyReviewsRequest
import com.streview.application.usecases.reviews.dto.PostReviewRequest
import com.streview.domain.commons.errors.ValidationError
import com.streview.domain.exceptions.InvalidInputException
import io.ktor.http.HttpStatusCode
import io.ktor.http.content.PartData
import io.ktor.http.content.forEachPart
import io.ktor.server.auth.UserIdPrincipal
import io.ktor.server.auth.principal
import io.ktor.server.plugins.origin
import io.ktor.server.request.receiveMultipart
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import io.ktor.utils.io.readBuffer
import kotlinx.io.Source
import org.koin.ktor.ext.inject
import java.math.BigDecimal

fun Route.reviewController() {
    /**
     * レビューを投稿する
     */
    val postReviewUseCase: PostReviewUseCase by inject()
    val getMyReviewsUseCase: GetMyReviewUseCase by inject()

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
        get("/post") {
            val userID = call.principal<UserIdPrincipal>()!!.name

            val input = GetMyReviewsRequest(
                userID = userID,
            )
            val res = getMyReviewsUseCase.execute(input)
            val origin = call.request.origin

            // 基本的な方法
            val baseUrl = "${origin.scheme}://${origin.serverHost}:${origin.serverPort}"
            val convert = res.copy(
                reviews = res.reviews.map {
                        review ->
                    review.copy(imageUrls = review.imageUrls.map { text -> baseUrl + text })
                }
            )
            call.respond(HttpStatusCode.OK, convert)
        }
    }

    /**
     * 自分がReReviewにしているレビューの一覧を取得する
     */
    get("/reviews/re-review") {
        TODO("Not yet implemented")
    }
}
