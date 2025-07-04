package com.streview.domain.commons.event

import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

open class DomainEvent(
    val eventTime: LocalDateTime = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
)