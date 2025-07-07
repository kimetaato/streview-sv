package com.streview.domain.relays

import com.streview.domain.commons.UserID

interface RelaysRepository {

    /**
     * ユーザーIDに紐づく Relays を取得する
     * @param userID 対象ユーザーのID
     * @return 該当する Relays（存在しない場合は null）
     */
    suspend fun findByUserId(userID: UserID): Relays?

    /**
     * Relays を保存または更新する
     * @param relays 保存対象の Relays モデル
     */
    suspend fun save(relays: Relays)
}
