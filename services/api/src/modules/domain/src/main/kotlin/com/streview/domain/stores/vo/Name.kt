package com.streview.domain.stores.vo

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import com.streview.domain.commons.errors.InvalidFormatRules
import com.streview.domain.commons.errors.ValidationError

/**
 * 飲食店の店舗名を定義する
 *
 * 文字列の前後に存在する空白文字はトリムする。文字中に含まれるものについては取り除かない。
 *
 * [MAX_LENGTH] 飲食店の最大文字数。100文字以下のみ許可をする
 *
 * [MIN_LENGTH] 飲食店の最小文字数。2文字以上にする
 */
class Name private constructor(
    val value: String
) {
    companion object {
        private const val MIN_LENGTH = 2
        private const val MAX_LENGTH = 100
        fun create(name: String): Result<Name, ValidationError> {
            val trimmedName = name.trim()
            return when {
                trimmedName.isBlank() -> Err(ValidationError.Required("name"))
                trimmedName.length < MIN_LENGTH -> {
                    Err(ValidationError.InvalidFormat("name", InvalidFormatRules.TOO_SHORT, trimmedName.length))
                }
                trimmedName.length > MAX_LENGTH -> {
                    Err(ValidationError.InvalidFormat("name", InvalidFormatRules.TOO_LONG, trimmedName.length))
                }
                else -> Ok(Name(trimmedName))
            }
        }
        fun reconstruct(name: String): Name {
            require(name.isNotBlank()) { "name cannot be blank" }
            require(name.length >= MIN_LENGTH) { "name is too short" }
            require(name.length <= MAX_LENGTH) { "name is too long" }
            return Name(name)
        }
    }
}
