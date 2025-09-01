package com.streview.domain.relays

import com.github.michaelbull.result.Result
import com.streview.domain.commons.UserID
import com.streview.domain.commons.errors.DomainError

interface RelayRepository {
    /**
     * ユーザーIDに紐づく Relays を取得する
     * @param userID 対象ユーザーのID
     * @return 該当する Relays（存在しない場合は null）
     */
    suspend fun findByUserIdAndReviewUUID(userID: String, reviewUUID: String): Result<Relay?, DomainError>

    suspend fun findByUserIDAndIsNotRead(userID: UserID): Result<List<Relay>, DomainError>

    /**
     * Relays を保存または更新する
     * @param relay 保存対象の Relays モデル
     */
    suspend fun save(relay: Relay): Result<Relay, DomainError>

    suspend fun saveAll(relays: List<Relay>): Result<List<Relay>, DomainError>

    /**
     * ユーザーが所有するRelayをリストで取得する
     */
    suspend fun findByUserId(userID: UserID): Result<List<Relay>, DomainError>

    /**
     * ユーザーが再共有に設定しているもののリスト
     */
    suspend fun findReReviewByUserId(userID: UserID): Result<List<Relay>, DomainError>
}
