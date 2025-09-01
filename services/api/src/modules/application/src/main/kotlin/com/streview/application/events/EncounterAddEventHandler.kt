package com.streview.application.events

import com.github.michaelbull.result.fold
import com.streview.domain.commons.UUID
import com.streview.domain.commons.UserID
import com.streview.domain.commons.event.EventHandler
import com.streview.domain.encounters.EncounterAddDomainEvent
import com.streview.domain.relays.Relay
import com.streview.domain.relays.RelayRepository
import com.streview.domain.reviews.ReviewRepository
import com.streview.domain.users.UserRepository
import com.streview.domain.visits.Visit
import com.streview.domain.visits.VisitRepository
import org.jetbrains.exposed.v1.r2dbc.transactions.suspendTransaction

/**
 * すれ違い1件毎に発生するドメインイベントを処理
 *
 * すれ違った時にどのレビューを受け取るべきかを判断する
 */
class EncounterAddEventHandler(
    private val userRepository: UserRepository,
    private val relayRepository: RelayRepository,
    private val reviewRepository: ReviewRepository,
    private val visitRepository: VisitRepository
) : EventHandler<EncounterAddDomainEvent> {
    override suspend fun handle(event: EncounterAddDomainEvent) {
        suspendTransaction {
            // 自身の共有優先度を取得
            val user = userRepository.findByID(event.actorID)

            // これまでに受け取ったレビューUUID
            val ownReviewUUIDs = relayRepository.findAllByUserId(event.actorID).fold(
                success = { it.map { review -> review.reviewUUID } },
                failure = { emptyList() }
            )

            // ReReviewから取得
            var storeUUID = determineReReview(event.actorID, event.encounterID, ownReviewUUIDs)
            storeUUID?.let {
                visitManager(event.actorID, storeUUID)
            }

            storeUUID = determineReview(event.actorID, event.encounterID, ownReviewUUIDs)
            visitManager(event.actorID, storeUUID)
        }
    }

    private suspend fun determineReReview(actorID: UserID, encounterID: UserID, ownReviewUUIDs: List<UUID>): UUID? {
        // すれ違い相手のリレビューから1件もらう
        val receiveReviewUUID = relayRepository.findReReviewByUserId(encounterID).fold(
            success = { reReviews ->
                reReviews.map { reReview -> reReview.reviewUUID }
                    .filterNot { it in ownReviewUUIDs }
                    .first() // 所有していないものから1件もらう
            },
            failure = {
                throw it
            }
        )

        val newRelay = Relay.factory(actorID.value, receiveReviewUUID.value)

        relayRepository.save(newRelay)
        return reviewRepository.findByUUID(receiveReviewUUID).fold(
            success = { review ->
                review?.storeUUID
            },
            failure = {
                throw it
            }
        )
    }

    private suspend fun determineReview(actorID: UserID, encounterID: UserID, ownReviewUUIDs: List<UUID>): UUID {
        // すれ違い相手のレビューから1件もらう
        val receiveReview = reviewRepository.findByWriterID(encounterID).fold(
            success = { reviews ->
                reviews
                    .filterNot { it.reviewUUID in ownReviewUUIDs }
                    .first() // 所有していないものから1件もらう
            },
            failure = {
                throw it
            }
        )

        val newRelay = Relay.factory(actorID.value, receiveReview.reviewUUID.value)

        relayRepository.save(newRelay)
        return receiveReview.storeUUID
    }

    private suspend fun visitManager(actorID: UserID, storeUUID: UUID) {
        visitRepository.findByUserIDAndStoreUUID(actorID, storeUUID).fold(
            success = { visit ->
                if (visit != null) {
                    visitRepository.save(Visit.create(actorID, storeUUID))
                }
            },
            failure = {
                throw it
            }
        )
    }
}
