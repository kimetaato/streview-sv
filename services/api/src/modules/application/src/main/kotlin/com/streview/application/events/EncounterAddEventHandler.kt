package com.streview.application.events

import com.streview.domain.commons.UUID
import com.streview.domain.commons.UserID
import com.streview.domain.commons.event.EventHandler
import com.streview.domain.encounters.EncounterAddDomainEvent
import com.streview.domain.relays.Relay
import com.streview.domain.relays.RelayRepository
import com.streview.domain.users.UserRepository
import kotlinx.coroutines.delay
import org.jetbrains.exposed.v1.r2dbc.transactions.suspendTransaction

class EncounterAddEventHandler(
    private val userRepository: UserRepository,
    private val relayRepository: RelayRepository
    // TODO: StoreRepositoryとReviewRepositoryが必要
) : EventHandler<EncounterAddDomainEvent> {
    override suspend fun handle(event: EncounterAddDomainEvent) {
        suspendTransaction {
            // 自身の共有優先度を取得
            val user = userRepository.findByID(event.actorID)
            if (user == null) {
                // イベント発行前に見てるので存在しえない
                return@suspendTransaction
            }
            // 自身の所有Reviewを取得
            val ownReviews = relayRepository.findAllByUserId(event.actorID)

            // ownReviews から自身の所有する reviewUUID のセットを作成
            val ownReviewUUIDs = ownReviews.map { it.reviewUUID }.toSet()

            // ReReviewから取得
            determineReReview(event.actorID, event.encounterID, ownReviewUUIDs)

            // TODO: Reviewから取得
            determineReview(event.actorID, event.encounterID, ownReviewUUIDs)
        }
    }

    private suspend fun determineReReview(actorID: UserID, encounterID: UserID, ownReviewUUIDs: Set<UUID>) {
        val encounterReReviews = relayRepository.findReReviewByUserId(encounterID)

        val reviewUUIDsNotInOwnReviews = encounterReReviews
            .map { it.reviewUUID } // encounterReReviews から reviewUUID だけを抽出
            .filterNot { it in ownReviewUUIDs } // ownReviewUUIDs に含まれていないものを選択

        // TODO: 追加のフィルターロジック 現在はランダム
        val reviewUUID = reviewUUIDsNotInOwnReviews.random()

        val newRelay = Relay.factory(actorID.value, reviewUUID.value)

        relayRepository.save(newRelay)
    }

    private suspend fun determineReview(actorID: UserID, encounterID: UserID, ownReviewUUIDs: Set<UUID>) {
        // TODO: 処理の実装
        println("actorID: $actorID")
        println("encounterID: $encounterID")
        println("ownReviewUUIDs: $ownReviewUUIDs")
        delay(10)
        return
    }
}
