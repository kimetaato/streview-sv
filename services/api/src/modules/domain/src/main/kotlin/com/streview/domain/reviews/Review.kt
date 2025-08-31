package com.streview.domain.reviews

import com.streview.domain.commons.UUID
import com.streview.domain.commons.UserID
import java.math.BigDecimal

class Review private constructor(
    val reviewUUID: UUID,
    val writerID: UserID,
    val storeUUID: UUID,
    val completedReview: CompletedReview,
) {
    companion object {
        fun create(
            writerID: UserID,
            storeUUID: UUID,
            completedReview: CompletedReview,
        ): Review {
            return Review(
                UUID.generate(),
                writerID,
                storeUUID,
                completedReview,
            )
        }
        fun reconstruct(
            reviewUUID: String,
            writerID: String,
            storeUUID: String,
            completedReview: CompletedReview,
        ): Review {
            return Review(
                UUID(reviewUUID),
                UserID(writerID),
                UUID(storeUUID),
                completedReview,
            )
        }
    }

    /**
     * 投稿済みレビューにコメントを追記する
     */
    fun postscript(text: String? = null, star: BigDecimal? = null): Review {
        val completedReview = completedReview.postscript(text, star)

        return Review(
            reviewUUID,
            writerID,
            storeUUID,
            completedReview,
        )
    }

    /**
     * レビューを非公開にする
     */
    fun setPrivate(): Review {
        val completedReview = completedReview.setPrivate()

        return Review(
            reviewUUID,
            writerID,
            storeUUID,
            completedReview,
        )
    }

    /**
     * レビューを公開にする
     */
    fun setPublic(): Review {
        val completedReview = completedReview.setPublic()

        return Review(
            reviewUUID,
            writerID,
            storeUUID,
            completedReview
        )
    }
}

@JvmInline
value class Comment(val value: String) {
    companion object {
        private const val MIN_LENGTH = 20
    }
    init {
        require(value.isNotEmpty()) { "コメントが入力されていません。" }
        require(value.length >= MIN_LENGTH) { "内容が短すぎます。" }
    }
}

@JvmInline
value class Star(val value: BigDecimal) {
    companion object {
        private val MAX_STAR = BigDecimal(5.0)
        private val MIN_STAR = BigDecimal(0.0)
    }
    init {
        require(value >= MIN_STAR && value <= MAX_STAR) { "不正な評価値です。" }
    }
}
