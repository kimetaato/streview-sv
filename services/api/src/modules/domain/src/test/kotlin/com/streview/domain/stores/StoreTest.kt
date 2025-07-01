package com.streview.domain.stores

import com.streview.domain.commons.UUID
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldHaveLength

class StoreTest : BehaviorSpec({
    Given("Storeのdomain model"){ // CONTEXT: どういう状況で、
        When("factoryメソッドで新しいStoreを作成する"){ // CONTEXT: どういう操作をしたとき、
            // CONTEXT: 準備
            val storeName: String = "かつお"

            // CONTEXT: 実行
            val store: Store = Store.factory(storeName)

            // CONTEXT: 確認
            Then("指定した店舗名がせっていされている"){ // CONTEXT: こうなるはずだ、という結果
                store.name.value shouldBe storeName
            }
            Then("36桁のIDが自動で生成されている"){
                store.id.value shouldHaveLength 36
            }
        }

        When("reconstructメソッドで既存（ここでは、このタイミングで作成した値）のStoreを再構築する"){
            val id: String = UUID.generate().value
            println(id)
            val name: String = "rebuild store name"

            val store = Store.reconstruct(id, name)

            Then("reconstructメソッドに与えた値が正しく設定されている"){
                store.id.value shouldBe id
                store.name.value shouldBe name
            }
        }
    }
})
