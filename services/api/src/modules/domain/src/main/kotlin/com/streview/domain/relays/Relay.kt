package com.streview.domain.relays

import com.streview.domain.commons.UUID
import com.streview.domain.commons.UserID
import com.streview.domain.exceptions.BadRequestException

/**
 * Relays ドメインモデル
 * - ユーザーが所有するレビューと、その公開設定を管理する
 */
class Relay private constructor(
    val userID: UserID,
    val reviewUUID: UUID,
    private var _isReReviewed: Boolean  // ステータスを変更することがあるけど、varで宣言すると外から値を変えられてしまうので、privateな変数として宣言して、getterで読み取り専用の値を渡す
) {
    val isReReviewed: Boolean
        get() = _isReReviewed

    companion object {
        /**
         * 新規作成
         */
        fun factory(userID: String, reviewUUID: String): Relay {
            return Relay(UserID(userID), UUID.generate(reviewUUID), false)
        }

        /**
         * 　DBなどからの再生成
         */
        fun reconstruct(userID: String, reviewUUID: String, isReReview: Boolean): Relay {
            return Relay(UserID(userID), UUID.generate(reviewUUID), false)
        }
    }

    /**
     * 振る舞いメソッド
     * RelayのステータスをReReview状態に変更する
     * internal修飾子を付与することによってapplicationレイヤーから参照されなくなり安全性が確保される
     */
    internal fun setReReview() {
        if (_isReReviewed) throw BadRequestException("既に設定されています。")
        _isReReviewed = true
    }

    /**
     * 振る舞いメソッド
     * RelayのステータスをReReview状態から解除する
     */
    public fun unsetReReview() {
        if (_isReReviewed.not()) throw BadRequestException("既に解除されています。")
        _isReReviewed = false
    }
}
