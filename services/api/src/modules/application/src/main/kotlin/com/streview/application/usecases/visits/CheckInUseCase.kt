package com.streview.application.usecases.visits

import com.github.michaelbull.result.andThen
import com.github.michaelbull.result.fold
import com.github.michaelbull.result.map
import com.streview.application.usecases.UseCase
import com.streview.application.usecases.visits.dto.CheckInRequest
import com.streview.application.usecases.visits.dto.CheckInResponse
import com.streview.domain.commons.UUID
import com.streview.domain.commons.UserID
import com.streview.domain.exceptions.InvalidInputException
import com.streview.domain.visits.VisitRepository

class CheckInUseCase(
    private val visitRepository: VisitRepository
) : UseCase<CheckInRequest, CheckInResponse> {
    override suspend fun execute(input: CheckInRequest): CheckInResponse {
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
            }.map { visit ->
                CheckInResponse(visit.storeUUID.value)
            }.fold(
                success = { response ->
                    return response
                },
                failure = { domainError ->
                    throw domainError
                }
            )
    }
}
