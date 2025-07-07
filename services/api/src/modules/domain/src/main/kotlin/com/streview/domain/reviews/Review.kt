package com.streview.domain.reviews

/**
 * Review 値オブジェクト
 * - reviewID: 一意なレビューID
 * - isRereview: 再レビュー可能（公開済み）フラグ
 */
data class Review(
    val reviewID: ReviewID,
    val isRereview: Boolean
)

/**
 * ReviewID 値オブジェクト
 * - String をラップし、型安全・バリデーションを提供
 */
@JvmInline
value class ReviewID(val value: String) {
    init {
        require(value.isNotBlank()) { "ReviewIDが不正な値です" }
    }
}
