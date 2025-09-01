package com.streview.domain.visits.vo

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import com.streview.domain.commons.errors.DomainError
import com.streview.domain.commons.errors.InvalidFormatRules
import com.streview.domain.commons.errors.ValidationError

enum class Status(val value: String) {
    Blocked("blocked"),
    Wanted("wanted"),
    Neutral("neutral");

    companion object {
        fun create(value: String): Result<Status, DomainError> =
            entries.find { it.value == value }?.let { status ->
                Ok(status)
            } ?: Err(ValidationError.InvalidFormat("status", InvalidFormatRules.PATTERN_MISMATCH, value))
    }
}
