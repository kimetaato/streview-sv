package com.streview.domain.exceptions

/**
 * アプリケーションのビジネスルールに関する例外の基底クラス。
 *
 * @param message エラーメッセージ
 * @param cause 元となった例外（例外チェーンのため）
 */
abstract class BusinessException(
    message: String,
    cause: Throwable? = null
) : RuntimeException(message, cause)

class NotFoundException(
    message: String,
    cause: Throwable? = null
) : BusinessException(message, cause)

class InvalidInputException(
    message: String,
    cause: Throwable? = null
) : BusinessException(message, cause)



