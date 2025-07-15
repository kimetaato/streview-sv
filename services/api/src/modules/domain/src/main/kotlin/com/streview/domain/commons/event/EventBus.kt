package com.streview.domain.commons.event

/**
 * ドメインイベントの発行と購読を管理するイベントバス
 * シングルトンパターンで実装
 */
object EventBus {
    private val handlers = mutableMapOf<String, MutableList<EventHandler<DomainEvent>>>()

    /**
     * 指定されたイベントタイプに対するハンドラーを登録する
     * @param eventType イベントの型
     * @param handler イベントハンドラー
     */
    @Suppress("UNCHECKED_CAST")
    fun <T : DomainEvent> subscribe(eventType: Class<T>, handler: EventHandler<T>) {
        handlers.getOrPut(eventType.simpleName) { mutableListOf() }
            .add(handler as EventHandler<DomainEvent>)
    }

    /**
     * ドメインイベントを発行し、登録されたハンドラーに通知する
     * @param event 発行するドメインイベント
     */
    suspend fun publish(event: DomainEvent) {
        val eventHandlers = handlers[event::class.simpleName] ?: return

        eventHandlers.forEach { handler ->
            try {
                handler.handle(event)
            } catch (e: Exception) {
                // ハンドラーのエラーで他のハンドラーの実行を停止しないよう継続
                System.err.println("EventHandler execution failed for ${event::class.simpleName}: ${e.message}")
            }
        }
    }

    /**
     * 指定されたイベントタイプのハンドラーをすべて削除する
     * テスト用途で使用
     * @param eventType クリアするイベントの型
     */
    fun <T : DomainEvent> clearHandlers(eventType: Class<T>) {
        handlers.remove(eventType.simpleName)
    }

    /**
     * すべてのハンドラーをクリアする
     * テスト用途で使用
     */
    fun clearAllHandlers() {
        handlers.clear()
    }
}
