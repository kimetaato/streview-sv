package com.streview.application.usecases.visits

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.andThen
import com.github.michaelbull.result.fold
import com.streview.application.usecases.UseCase
import com.streview.common.dto.stores.VisitStatusRequest
import com.streview.common.dto.stores.VisitStatusResponse
import com.streview.domain.commons.UUID
import com.streview.domain.commons.UserID
import com.streview.domain.commons.errors.EntityError
import com.streview.domain.visits.VisitRepository
import com.streview.domain.visits.vo.Status
import org.jetbrains.exposed.v1.r2dbc.transactions.suspendTransaction

class VisitStatusUseCase(
    private val visitRepository: VisitRepository
) : UseCase<VisitStatusRequest, VisitStatusResponse> {
    override suspend fun execute(input: VisitStatusRequest): VisitStatusResponse =
        suspendTransaction {
            val userID = UserID(input.userID)
            val storeUUID = UUID.generate(input.storeUUID)
            Status.create(input.status)
                .andThen { status ->
                    visitRepository.findByUserIDAndStoreUUID(userID, storeUUID)
                        .andThen { visit ->
                            visit?.let { visit ->
                                Ok(visit)
                            } ?: Err(EntityError.NotFound("visit"))
                        }.andThen { visit ->
                            visit.setStatus(status)
                            visitRepository.save(visit)
                        }
                }
        }.fold(
            success = { visit ->
                VisitStatusResponse(
                    visit.storeUUID.value,
                )
            },
            failure = { domainError ->
                throw domainError
            }
        )
}
