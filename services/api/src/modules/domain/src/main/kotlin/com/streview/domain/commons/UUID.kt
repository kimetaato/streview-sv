package com.streview.domain.commons

import java.util.UUID as StreviewUuid


@JvmInline
value class UUID(val value: String) {
    init {
        require(value.isNotEmpty()) { "moji wo kure" }
    }

    companion object {
        // UUIDをデータベースなどから取得し再生成する場合
        fun generate(uuid: String): UUID {
            return UUID(uuid)
        }

        // UUIDを新規作成する場合
        fun generate(): UUID {
            return UUID(StreviewUuid.randomUUID().toString())
        }
    }
}


