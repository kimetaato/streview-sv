package com.streview.domain.relays

import com.streview.domain.commons.UserID

interface RelayRepository {
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

    /**
     * ユーザーが所有するRelayをリストで取得する
     */
    suspend fun findAllByUserId(userID: UserID): List<Relay>

    /**
     * ユーザーが再共有に設定しているもののリスト
     */
    suspend fun findReReviewByUserId(userID: UserID): List<Relay>
}
