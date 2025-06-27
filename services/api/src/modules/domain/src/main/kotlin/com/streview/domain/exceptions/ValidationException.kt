package com.streview.domain.exceptions

import kotlinx.serialization.Serializable


/**
 * バリデーションエラーの詳細を収集する例外クラス
 * @param message エラーメッセージ
 * @param validationErrors 項目ごとのバリデーションエラーの詳細
 * @param cause 元となったエラー
 */
class ValidationException(
    message: String,
    val validationErrors: List<ValidationError>,
    cause: Throwable? = null
) : BusinessException(message, cause)


/**
 * 値オブジェクトの生成エラーの詳細
 * @param fieldName バリデーションエラーの対象項目
 * @param message バリデーションエラーの詳細
 */
@Serializable
class ValidationError(
    val fieldName: String,
    val message: String
)

/**
 * バリデーションエラーを生成して保持するヘルパー関数
 * @param fieldName バリデーションエラーの対象項目
 * @param message バリデーションエラーの詳細
 */
fun MutableList<ValidationError>.addError(fieldName: String, message: String?) {
    add(ValidationError(fieldName, message ?: "不正な入力値です"))
}

