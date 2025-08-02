package com.streview.domain.stores.vo

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import com.streview.domain.commons.errors.InvalidFormatRules
import com.streview.domain.commons.errors.ValidationError

/**
 * 飲食店の基本的な情報、概要
 *
 * 前後の空白文字を取り除く、文中に含まれるものについては取り除かない
 *
 * [MIN_LENGTH] 概要の最小文字数。10文字以上の基本情報を必要とする。
 *
 * [MAX_LENGTH] 概要の最大文字数。1024文字以上は長すぎるのでNG
 */
class Description private constructor(
    val value: String
) {
    companion object {
        private const val MIN_LENGTH = 10
        private const val MAX_LENGTH = 1024

        fun create(description: String): Result<Description, ValidationError> {
            val trimmedDescription = description.trim()
            return when {
                trimmedDescription.isBlank() -> Err(ValidationError.Required("description"))
                trimmedDescription.length < MIN_LENGTH -> Err(
                    ValidationError.InvalidFormat(
                        "description",
                        InvalidFormatRules.TOO_SHORT,
                        trimmedDescription.length
                    )
                )
                trimmedDescription.length > MAX_LENGTH -> Err(
                    ValidationError.InvalidFormat("description", InvalidFormatRules.TOO_LONG, trimmedDescription.length)
                )
                else -> Ok(Description(trimmedDescription))
            }
        }
        fun reconstruct(description: String): Description {
            require(description.isNotBlank()) { "description cannot be blank" }
            require(description.length >= MIN_LENGTH) { "description is too short" }
            require(description.length <= MAX_LENGTH) { "description is too long" }
            return Description(description)
        }
    }
}
