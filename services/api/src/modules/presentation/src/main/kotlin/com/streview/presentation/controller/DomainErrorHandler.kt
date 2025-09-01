package com.streview.presentation.controller

import com.streview.domain.commons.errors.DomainError
import com.streview.domain.commons.errors.EntityError
import com.streview.domain.commons.errors.TechnicalError
import com.streview.domain.commons.errors.ValidationError

fun DomainError.handler() {
    when (this) {
        EntityError.AlreadyExist -> TODO()
        is EntityError.NotFound -> TODO()
        is TechnicalError.DatabaseError -> TODO()
        is TechnicalError.ExternalServiceError -> TODO()
        is TechnicalError.NetworkError -> TODO()
        is ValidationError.InvalidFormat -> TODO()
        is ValidationError.Multiple -> TODO()
        is ValidationError.Required -> TODO()
    }
}
