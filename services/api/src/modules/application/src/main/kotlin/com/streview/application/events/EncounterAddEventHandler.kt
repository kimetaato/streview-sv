package com.streview.application.events

import com.streview.domain.commons.UUID
import com.streview.domain.commons.UserID
import com.streview.domain.commons.event.EventHandler
import com.streview.domain.encounters.EncounterAddDomainEvent
import com.streview.domain.relays.Relay
import com.streview.domain.relays.RelayRepository
import com.streview.domain.users.UserRepository

class EncounterAddEventHandler(
    private val userRepository: UserRepository,
    private val relayRepository: RelayRepository
    // TODO: StoreRepositoryとReviewRepositoryが必要
) : EventHandler<EncounterAddDomainEvent> {
    override suspend fun handle(event: EncounterAddDomainEvent) {
        // 自身の共有優先度を取得
        val user = userRepository.findByID(event.actorID)
        if (user == null) {
            // イベント発行前に見てるので存在しえない
            return
        }
        /**
         * HACK: 現状のテーブル設計だと複雑なステップで絞り込み条件に合致したレビューを探す必要がある
         *  1. 自身の優先度取得
         *  2. 投稿したレビュー一覧を取得
         *  3. 取得条件に応じて、追加情報取得(現在地: 店舗位置, いいね: Relayの数, エリア: 両方)
         *  4. 追加情報を元にフィルターする
         *  5. これを投稿したレビュー、再共有ビューで2回行う必要がある。
         *
         *  TODO: リードモデル作って解決したい。
         *   ReviewUUID, GeoLocation, iSReReviewCount
         * */
        factoryNewRely(user.userID, UUID.generate())
    }

    private suspend fun factoryNewRely(userID: UserID, reviewUUID: UUID) {
        val newRelay = Relay.factory(userID.value, reviewUUID.value)

        relayRepository.save(newRelay)
    }
}
