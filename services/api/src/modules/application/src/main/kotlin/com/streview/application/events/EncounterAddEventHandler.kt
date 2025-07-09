package com.streview.application.events

import com.streview.domain.commons.event.EventHandler
import com.streview.domain.encounters.EncounterAddDomainEvent

class EncounterAddEventHandler(
    // TODO: 登録したいリポジトリとかあれば引数に設定
) : EventHandler<EncounterAddDomainEvent> {
    override suspend fun handle(event: EncounterAddDomainEvent) {
        // イベントから値を取り出してログ出力
        println("EncounterAddEventHandler called!")
        println("Actor ID: ${event.actorID.value}")
        println("Encounter ID: ${event.encounterID.value}")
        println("Encounter Date: ${event.encounterDate.value}")

        // TODO: 必要に応じて以下のような処理を実装
        // - 通知の送信
        // - 統計情報の更新
        // - 他のサービスへの連携
    }
}
