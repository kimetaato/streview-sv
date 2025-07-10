package com.streview.domain.reviews

import com.streview.domain.commons.UserID
import com.streview.domain.exceptions.ValidationError
import com.streview.domain.exceptions.ValidationException
import com.streview.domain.exceptions.addError
import com.streview.domain.stores.Id
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import java.util.*

/**
 * Review エンティティ。
 * 投稿レビューの情報をドメインとして保持する。
 */
data class Review(
    val reviewID: ReviewID,
    val title: Title,
    val comment: Comment,
    val star: Star,
    val isRereview: Boolean,
    val userID: UserID,
    val storeID: Id,
    val createdAt: CreatedAt,

) {
    companion object {
        /**
         * バリデーション付きの生成関数。
         * 値オブジェクトの生成時に失敗があれば、ValidationException を投げる。
         */
        fun create(
            reviewIDString: String,
            titleInput: String,
            commentInput: String,
            starInput: Int,
            userID: UserID,
            storeID: Id,

        ): Review {
            val errors: MutableList<ValidationError> = mutableListOf()

            val validatedReviewID: ReviewID? = try {
                ReviewID(reviewIDString)
            } catch (e: IllegalArgumentException) {
                errors.addError("reviewID", e.message)
                null
            }

            val validatedTitle: Title? = try {
                Title(titleInput)
            } catch (e: IllegalArgumentException) {
                errors.addError("title", e.message)
                null
            }

            val validatedComment: Comment? = try {
                Comment(commentInput)
            } catch (e: IllegalArgumentException) {
                errors.addError("comment", e.message)
                null
            }

            val validatedStar: Star? = try {
                Star(starInput)
            } catch (e: IllegalArgumentException) {
                errors.addError("star", e.message)
                null
            }


            if (errors.isNotEmpty()) {
                throw ValidationException("レビュー作成時にバリデーションエラーが発生しました。", errors)
            }

            return Review(
                reviewID = validatedReviewID!!,
                title = validatedTitle!!,
                comment = validatedComment!!,
                star = validatedStar!!,
                isRereview = false,
                userID = userID,
                storeID = storeID,
                createdAt = CreatedAt.now(),

            )
        }
    }
}

/**
 * Review の ID 値オブジェクト（UUID）
 */
@JvmInline
value class ReviewID(val value: String) {
    companion object {
        fun generate(): ReviewID = ReviewID(UUID.randomUUID().toString())
    }

    init {
        require(value.length == 36) { "レビューIDの形式が不正です。" }
    }
}

/** タイトルの値オブジェクト */
@JvmInline
value class Title(val value: String) {
    init {
        require(value.isNotBlank()) { "タイトルは必須です。" }
        require(value.length <= 100) { "タイトルは100文字以下にしてください。" }
    }
}

/** コメントの値オブジェクト */
@JvmInline
value class Comment(val value: String) {
    init {
        require(value.isNotBlank()) { "コメントは必須です。" }
        require(value.length <= 1000) { "コメントは1000文字以下にしてください。" }
    }
}

/** スター評価（0〜5） */
@JvmInline
value class Star(val value: Int) {
    init {
        require(value in 0..5) { "評価は0〜5の範囲である必要があります。" }
    }
}

/** 投稿日時（現在時刻を使って生成） */
@JvmInline
value class CreatedAt(val value: String) {
    companion object {
        fun now(): CreatedAt {
            val currentTime = Clock.System.now()
            val datetime = currentTime.toLocalDateTime(TimeZone.currentSystemDefault())
            return CreatedAt(datetime.toString())
        }
    }
}

