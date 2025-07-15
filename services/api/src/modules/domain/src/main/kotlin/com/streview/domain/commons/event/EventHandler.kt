package com.streview.domain.commons.event

// イベントハンドラー
fun interface EventHandler<T : DomainEvent> {
    suspend fun handle(event: T)
}
