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
 * [MAX_LENGTH] 飲食店の最大文字数。150文字以下に制限する
 */
class Address private constructor(
    val value: String
) {
    companion object {
        private const val MAX_LENGTH = 150

        fun create(address: String): Result<Address, ValidationError> {
            val trimmedAddress = address.trim()
            return when {
                trimmedAddress.isBlank() -> Err(ValidationError.Required("address"))
                trimmedAddress.length > MAX_LENGTH -> {
                    Err(ValidationError.InvalidFormat("address", InvalidFormatRules.TOO_LONG, trimmedAddress.length))
                }
                else -> Ok(Address(trimmedAddress.trim()))
            }
        }
        fun reconstruct(address: String): Address {
            require(address.isNotBlank()) { "address cannot be blank" }
            require(address.length <= MAX_LENGTH) { "address is too long" }
            return Address(address)
        }
    }
}
