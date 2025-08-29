package com.streview.domain.stores.vo

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import com.streview.domain.commons.errors.InvalidFormatRules
import com.streview.domain.commons.errors.ValidationError

class Genre private constructor(
    val value: String,
) {
    companion object {
        private const val MAX_LENGTH = 15

        fun create(genre: String): Result<Genre, ValidationError> {
            val trimmedGenre = genre.trim()
            return when {
                trimmedGenre.isBlank() -> Err(ValidationError.Required("genre"))
                trimmedGenre.length > MAX_LENGTH -> {
                    Err(ValidationError.InvalidFormat("genre", InvalidFormatRules.TOO_LONG, trimmedGenre.length))
                }
                else -> Ok(Genre(trimmedGenre))
            }
        }

        fun reconstruct(value: String): Genre {
            require(value.isNotBlank()) { "genre cannot be blank" }
            require(value.length <= MAX_LENGTH) { "genre is too long" }
            return Genre(value)
        }
    }
}
