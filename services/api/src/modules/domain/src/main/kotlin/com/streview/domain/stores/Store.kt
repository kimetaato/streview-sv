package com.streview.domain.stores

import java.util.UUID
import kotlin.math.max

data class Store(
    val id: Id,
    val name: Name,
){
    companion object{
        fun factory(name: String): Store{
            return Store(
                id = Id.generate(),
                name = Name(name),
            )
        }
        fun reconstruct(id: String, name: String): Store{
            return Store(
                id = Id(id),
                name = Name(name),
            )
        }
    }
}

// 値オブジェクト

@JvmInline
value class Id(val value: String){ // TODO: UUIDを生成すべき
    companion object{
        val length = 36

        fun generate(): Id{
            val newId = UUID.randomUUID().toString()
            return Id(newId)
        }
    }
    init {
         require(value.length == length){ "入力値が不正です。" }
    }
}

@JvmInline
value class Name(val value: String){
    init {

    }
}