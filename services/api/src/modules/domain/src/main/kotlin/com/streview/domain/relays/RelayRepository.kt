package com.streview.domain.relays


interface RelaysRepository {

    /**
     * ユーザーIDに紐づく Relays を取得する
     * @param userID 対象ユーザーのID
     * @return 該当する Relays（存在しない場合は null）
     */
    suspend fun findByUserIdAndReviewUUID(userID: String, reviewUUID: String): Relay?

    /**
     * Relays を保存または更新する
     * @param relay 保存対象の Relays モデル
     */
    suspend fun save(relay: Relay)
}
