package com.streview.service.relays

import com.streview.domain.relays.Relay

class RelaysDomainService(
    // レビューのリポジトリ
    // ストアとユーザーの関係リポジトリ
) {
    suspend fun reReview(relay: Relay): Relay {
        // Reviewの対象店舗を取得
        // 対象店舗への来店履歴を取得

        // いけてたらステータスを変えて返す　*保存はusecaseで行う
        relay.setReReview()
        return relay
    }
}