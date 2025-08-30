package com.streview.domain.stores.vo

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import com.streview.domain.commons.errors.InvalidFormatRules
import com.streview.domain.commons.errors.ValidationError

class Open private constructor(
    val value: String
) {
    companion object {
        private const val MAX_LENGTH = 255
        fun create(open: String): Result<Open, ValidationError> {
            val trimmedOpen = open.trim()
            return when {
                trimmedOpen.isBlank() -> Err(ValidationError.Required("open"))
                trimmedOpen.length > MAX_LENGTH -> {
                    Err(ValidationError.InvalidFormat("open", InvalidFormatRules.TOO_LONG, trimmedOpen.length))
                }
                else -> Ok(Open(trimmedOpen))
            }
        }
        fun reconstruct(open: String): Open {
            require(open.isNotBlank()) { "open cannot be blank" }
            require(open.length <= MAX_LENGTH) { "open is too long" }
            return Open(open)
        }
    }
}
