package com.streview.domain.stores

import com.streview.domain.commons.UUID
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldHaveLength

class StoreTest : BehaviorSpec({
    Given("Store domain model"){ // CONTEXT: どういう状況で、
        When("execute factory func"){ // CONTEXT: どういう操作をしたとき、
            // CONTEXT: 準備
            val storeName: String = "かつお"

            // CONTEXT: 実行
            val store: Store = Store.factory(storeName)

            // CONTEXT: 確認
            Then("setting value"){ // CONTEXT: こうなるはずだ、という結果
                store.name.value shouldBe storeName
            }
            Then("auto gen id"){
                store.id.value shouldHaveLength 36
            }
        }

        When("rebuild existing store"){
            val id: String = UUID.generate().value
            println(id)
            val name: String = "rebuild store name"

            val store = Store.reconstruct(id, name)

            Then("setting value"){
                store.id.value shouldBe id
                store.name.value shouldBe name
            }
        }
    }
})
