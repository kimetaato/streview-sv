package com.streview.usecase.relays

import com.streview.application.usecases.UseCase
import com.streview.domain.commons.UserID
import com.streview.domain.exceptions.InvalidInputException
import com.streview.domain.exceptions.NotFoundException
import com.streview.domain.reviews.ReviewID
import com.streview.domain.relays.RelaysRepository
import com.streview.usecase.relays.dto.MarkReviewAsRereviewedRequest
import com.streview.usecase.relays.dto.MarkReviewAsRereviewedResponse

/**
 * レビューの公開状態（isRereview）を true に変更するユースケース。
 * ドメイン層の Relays モデルを通じて処理を行う。
 */
class MarkReviewAsRereviewedUseCase(
    private val repository: RelaysRepository
) : UseCase<MarkReviewAsRereviewedRequest, MarkReviewAsRereviewedResponse> {

    override suspend fun execute(input: MarkReviewAsRereviewedRequest): MarkReviewAsRereviewedResponse {
        val userID = UserID(input.userId)
        val reviewID = ReviewID(input.reviewId)

        // ユーザーの Relays を取得。存在しなければ例外を投げる。
        val relays = repository.findByUserId(userID)
            ?: throw NotFoundException("ユーザーに対応するレビューが存在しません: ${input.userId}")

        // 指定されたレビューIDが存在しなければエラー
        if (!relays.containsReview(reviewID)) {
            throw InvalidInputException("指定されたレビューIDは存在しません: ${input.reviewId}")
        }

        // ドメインモデル上でレビューの再公開処理を実行
        val updatedRelays = relays.markRereviewed(reviewID)

        // 更新された Relays を保存
        repository.save(updatedRelays)

        // 正常終了
        return MarkReviewAsRereviewedResponse(
            success = true,
            message = "レビューを公開状態に変更しました。"
        )
    }
}