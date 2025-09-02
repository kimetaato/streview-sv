package com.streview.application.usecases.visits

import com.github.michaelbull.result.andThen
import com.github.michaelbull.result.fold
import com.streview.application.usecases.UseCase
import com.streview.common.dto.stores.CheckInRequest
import com.streview.common.dto.stores.CheckInResponse
import com.streview.domain.commons.UUID
import com.streview.domain.commons.UserID
import com.streview.domain.exceptions.InvalidInputException
import com.streview.domain.visits.VisitRepository
import org.jetbrains.exposed.v1.r2dbc.transactions.suspendTransaction

class CheckInUseCase(
    private val visitRepository: VisitRepository
) : UseCase<CheckInRequest, CheckInResponse> {
    override suspend fun execute(input: CheckInRequest): CheckInResponse =
        suspendTransaction {
            val userID = UserID(input.userID)
            val storeUUID = UUID.generate(input.storeUUID)
            visitRepository.findByUserIDAndStoreUUID(userID, storeUUID)
                .andThen { visit ->
                    visit?.let {
                        it.incrementVisitCount()
                        visitRepository.save(it)
                    } ?: throw InvalidInputException(
                        "対象の飲食店が見つかりません",
                    )
                }
        }.fold(
            success = { visit ->
                CheckInResponse(visit.storeUUID.value)
            },
            failure = { domainError ->
                throw domainError
            }
        )
}
