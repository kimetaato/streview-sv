package com.streview.domain.reviews

import com.streview.domain.commons.UUID
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import java.math.BigDecimal

class CompletedReview private constructor(
    val comment: Comment,
    val star: Star,
    val imageUUIDs: List<UUID>,
    val isPublic: Boolean,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
) {
    companion object {
        fun create(comment: String, star: BigDecimal, imageUUIDs: List<UUID>): CompletedReview { // 新規作成
            val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
            return CompletedReview(
                Comment(comment),
                Star(star),
                imageUUIDs,
                true,
                now,
                now
            )
        }

        fun reconstruct(
            comment: String,
            star: BigDecimal,
            imageUUIDs: List<String>,
            isPublic: Boolean,
            createdAt: LocalDateTime,
            updatedAt: LocalDateTime
        ): CompletedReview {
            return CompletedReview(
                Comment(comment),
                Star(star),
                imageUUIDs.map { imageUUID -> UUID(imageUUID) },
                isPublic,
                createdAt,
                updatedAt
            )
        }
    }

    /**
     * オプションとして引数を受け取り自身のコピーを作成する。
     * 変更不可である作成日は更新されない。
     * 変更されたタイミングで更新日も変更される。
     */
    private fun copy(
        comment: String = this.comment.value,
        star: BigDecimal = this.star.value,
        imageUUIDs: List<String> = this.imageUUIDs.map { it.value },
        isPublic: Boolean = this.isPublic,
    ): CompletedReview {
        val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())

        return CompletedReview(
            Comment(comment),
            Star(star),
            imageUUIDs.map { imageUUID -> UUID(imageUUID) },
            isPublic,
            this.createdAt,
            now
        )
    }

    /**
     * コメントを追記した新しいエンティティを生成。更新日を現在時刻に変更
     * @param text 追記する内容
     */
    internal fun postscript(text: String? = null, star: BigDecimal? = null): CompletedReview {
        var newComment = comment.value
        if (!text.isNullOrEmpty()) {
            newComment += "\n" + text
        }
        val newStar = star ?: this.star.value

        return copy(comment = newComment, star = newStar)
    }

    /**
     * レビューを非公開にする
     */
    internal fun setPrivate(): CompletedReview {
        return copy(isPublic = false)
    }

    /**
     * レビューを公開にする
     */
    internal fun setPublic(): CompletedReview {
        return copy(isPublic = true)
    }
}
