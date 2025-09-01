package com.streview.application.usecases.relays

import com.github.michaelbull.result.fold
import com.streview.application.usecases.UseCase
import com.streview.application.usecases.relays.dto.RelayStatusToggleRequest
import com.streview.application.usecases.relays.dto.RelayStatusToggleResponse
import com.streview.domain.exceptions.InvalidInputException
import com.streview.domain.relays.RelayRepository
import com.streview.service.relays.RelayDomainService
import org.jetbrains.exposed.v1.r2dbc.transactions.suspendTransaction

/**
 * 受け取ったレビューに対して、再共有状態(ReReview)に設定するユースケース
 */
class MarkRelayStatusUseCase(
    private val repository: RelayRepository,
    private val domainService: RelayDomainService,
) : UseCase<RelayStatusToggleRequest, RelayStatusToggleResponse> {
    override suspend fun execute(input: RelayStatusToggleRequest): RelayStatusToggleResponse =
        suspendTransaction {
            repository.findByUserIdAndReviewUUID(input.userID, input.reviewUUID).fold(
                success = { relay ->
                    if (relay == null) {
                        throw InvalidInputException(message = "対象のレビューが存在しません。")
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
                    RelayStatusToggleResponse(
                        toggledRelay.reviewUUID.value,
                    )
                },
                failure = { domainError ->
                    throw domainError
                }
            )
        }
}
