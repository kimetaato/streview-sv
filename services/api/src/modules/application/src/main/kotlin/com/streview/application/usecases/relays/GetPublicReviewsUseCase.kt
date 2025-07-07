package com.streview.usecase.relays

import com.streview.application.usecases.UseCase
import com.streview.domain.commons.UserID
import com.streview.domain.exceptions.NotFoundException
import com.streview.domain.relays.RelaysRepository
import com.streview.usecase.relays.dto.GetPublicReviewsRequest
import com.streview.usecase.relays.dto.GetPublicReviewsResponse

/**
 * 特定ユーザーが所有する「公開状態」のレビュー（isRereview = true）のみを取得するユースケース。
 */
class GetPublicReviewsUseCase(
    private val repository: RelaysRepository
) : UseCase<GetPublicReviewsRequest, GetPublicReviewsResponse> {

    override suspend fun execute(input: GetPublicReviewsRequest): GetPublicReviewsResponse {
        val userID = UserID(input.userId)

        // ユーザーに紐づく Relays を取得。存在しない場合は例外を投げる
        val relays = repository.findByUserId(userID)
            ?: throw NotFoundException("指定されたユーザーのレビューが見つかりません: ${input.userId}")

        // 公開レビューのみを抽出
        val publicReviews = relays.getPublicReviews()

        return GetPublicReviewsResponse(
            reviewIds = publicReviews.map { it.reviewID.value }
        )
    }
}
