package com.streview.domain.relays

import com.streview.domain.commons.UserID
import com.streview.domain.reviews.Review
import com.streview.domain.reviews.ReviewID
import java.util.*

/**
 * Relays ドメインモデル
 * - ユーザーが所有するレビューと、その公開設定を管理する
 */
data class Relays(
    val userID: UserID,
    val reviews: List<Review>
) {
    companion object {
        /**
         * レビューIDのリストから初期状態のレビューを生成
         */
        fun factory(userID: String, reviewIds: List<String>): Relays {
            val reviewList = reviewIds.map { id ->
                Review(
                    reviewID = ReviewID(id),
                    isRereview = false
                )
            }
            return Relays(UserID(userID), reviewList)
        }

        /**
         * DBなどから復元する場合に使用
         */
        fun reconstruct(userID: String, reviews: List<Review>): Relays {
            return Relays(UserID(userID), reviews)
        }
    }

    /**
     * 指定されたレビューIDのレビューを「再公開済み」にする
     */
    fun markRereviewed(reviewID: ReviewID): Relays {
        val updated = reviews.map {
            if (it.reviewID == reviewID) it.copy(isRereview = true) else it
        }
        return this.copy(reviews = updated)
    }

    /**
     * 公開済みのレビューだけを取得
     */
    fun getPublicReviews(): List<Review> {
        return reviews.filter { it.isRereview }
    }

    /**
     * 指定されたレビューIDが存在するか確認
     */
    fun containsReview(reviewID: ReviewID): Boolean {
        return reviews.any { it.reviewID == reviewID }
    }

    /**
     * Relays に一意なIDを持たせる場合のための値オブジェクト（今は未使用）
     */
    @JvmInline
    value class Id(val value: String) {
        companion object {
            const val length = 36
            fun generate(): Id {
                return Id(UUID.randomUUID().toString())
            }
        }

        init {
            require(value.length == length) { "入力値が不正です。" }
        }
    }
}

/**
 * ドメインイベント：レビューが再公開状態になったことを示す
 * - ユースケース側でこのイベントを生成し、副作用処理に使う
 */
//data class ReviewMarkedAsRereviewed(
//    val userID: UserID,
//    val reviewID: ReviewID
//)
