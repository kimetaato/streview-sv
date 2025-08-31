package com.streview.domain.commons.errors

sealed class DomainError() : Throwable() {
    override val message: String? = "Domain Error" // HACK: StatusPageによって一括でエラーハンドリングをするために継承。責務の観点から見ると綺麗な実装ではない
    override val cause: Throwable? = null
}

enum class InvalidFormatRules {
    PATTERN_MISMATCH, // パターンに一致していない
    NOT_NUMERIC, // 数値ではない
    NOT_ALPHANUMERIC, // 英数字のみでない
    INVALID_DATE_TIME, // 日付がおかしいよ
    TOO_LONG, // 長すぎる
    TOO_SHORT, // 短すぎる
    // 必要に応じて他のルールを追加
}

/**
 * バリデーション時に発生するエラーを定義したクラス。
 * 複数のバリデーションを同時に行う場合はMultipleにエラーを収集する
 */
sealed class ValidationError : DomainError() {
    data class Required(val fieldName: String) : ValidationError()
    data class InvalidFormat(val fieldName: String, val rule: InvalidFormatRules, val value: Any) : ValidationError()
    data class Multiple(val errors: List<ValidationError>) : ValidationError()

    fun toFlatValidationErrors(): List<ValidationError> {
        return when (this) {
            is Multiple -> this.errors.flatMap { it.toFlatValidationErrors() }
            else -> listOf(this)
        }
    }
}

/**
 * エンティティのライフサイクル上不可能な状態の場合に発生するエラー
 */
sealed class EntityError : DomainError() {
    data class NotFound(val type: String) : EntityError()
    object AlreadyExist : EntityError()
}

/**
 * 技術的なエラーを抽象的なドメインのエラーとして扱う
 */
sealed class TechnicalError : DomainError() {
    data class DatabaseError(
        val isConnectionIssue: Boolean = false,
        override val cause: Throwable? = null
    ) : TechnicalError()

    // ネットワーク関連の問題
    data class NetworkError(
        val serviceName: String,
        val isTimeout: Boolean = false,
        override val cause: Throwable? = null,
    ) : TechnicalError()

    // 外部サービス関連の問題
    data class ExternalServiceError(
        val serviceName: String,
        override val cause: Throwable? = null,
        val statusCode: Int? = null
    ) : TechnicalError()
}
