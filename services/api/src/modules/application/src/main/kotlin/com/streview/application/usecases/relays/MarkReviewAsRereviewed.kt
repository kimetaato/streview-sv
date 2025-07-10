package com.streview.application.usecases.relays

import com.streview.application.usecases.UseCase
import com.streview.application.usecases.relays.dto.RelayStatusToggleRequest
import com.streview.application.usecases.relays.dto.RelayStatusToggleResponse
import com.streview.domain.exceptions.BadRequestException
import com.streview.domain.relays.RelaysRepository
import com.streview.service.relays.RelayDomainService

/**
 * 受け取ったレビューに対して、再共有状態(ReReview)に設定するユースケース
 */
class MarkReviewAsRereviewedUseCase(
    private val repository: RelaysRepository,
    private val domainService: RelayDomainService,
) : UseCase<RelayStatusToggleRequest, RelayStatusToggleResponse> {
    override suspend fun execute(input: RelayStatusToggleRequest): RelayStatusToggleResponse {
        // Relayを取得
        val relay = repository.findByUserIdAndReviewUUID(input.userID, input.reviewUUID)
        if (relay == null) {
            throw BadRequestException(message = "対象のレビューが存在しません。")
        }

        // ステータスの更新を行う
        val toggledRelay = if (input.toggleStatus) {
            domainService.reReview(relay)
        } else {
            relay.unsetReReview()
            relay
        }

        // 更新された Relays を保存
        repository.save(toggledRelay)

        // 正常終了
        return RelayStatusToggleResponse(
            toggledRelay.reviewUUID.value,
        )
    }
}