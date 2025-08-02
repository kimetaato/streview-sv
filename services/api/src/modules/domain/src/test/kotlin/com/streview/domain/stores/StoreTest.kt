package com.streview.domain.stores

import com.streview.domain.commons.errors.ValidationError
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldHaveLength

class StoreTest : FreeSpec({
    "Storeのdomain model" - {
        "createメソッドで新しいStoreを作成する" - {
            "正常な値で作成成功" {
                val result = Store.create(
                    name = "テスト店舗",
                    genre = "イタリアン",
                    address = "東京都渋谷区",
                    tel = "03-1234-5678",
                    description = "素晴らしいお店です。料理が美味しくて雰囲気も良いです。",
                    open = "11:00-22:00",
                    latitude = 35.6762,
                    longitude = 139.6503
                )

                result.isOk shouldBe true
                result.value.name.value shouldBe "テスト店舗"
                result.value.genre.value shouldBe "イタリアン"
                result.value.address.value shouldBe "東京都渋谷区"
                result.value.tel.value shouldBe "03-1234-5678"
                result.value.description.value shouldBe "素晴らしいお店です。料理が美味しくて雰囲気も良いです。"
                result.value.open.value shouldBe "11:00-22:00"
                result.value.geoLocation.latitude shouldBe 35.6762
                result.value.geoLocation.longitude shouldBe 139.6503
                result.value.storeUUID.value shouldHaveLength 36
            }

            "無効な値で作成失敗" {
                val result = Store.create(
                    name = "",
                    genre = "",
                    address = "",
                    tel = "invalid-tel",
                    description = "短い",
                    open = "",
                    latitude = 91.0,
                    longitude = 181.0
                )

                result.isErr shouldBe true
                val error = result.error
                (error is ValidationError.Multiple) shouldBe true
                val multipleError = error as ValidationError.Multiple
                multipleError.errors.size shouldBe 8
            }
        }

        "reconstructメソッドで既存のStoreを再構築する" {
            val store = Store.reconstruct(
                storeUUID = "550e8400-e29b-41d4-a716-446655440000",
                name = "再構築店舗",
                genre = "フレンチ",
                address = "大阪府大阪市",
                tel = "06-1234-5678",
                description = "再構築されたお店です。歴史があります。",
                open = "10:00-21:00",
                latitude = 34.6937,
                longitude = 135.5023
            )

            store.storeUUID.value shouldBe "550e8400-e29b-41d4-a716-446655440000"
            store.name.value shouldBe "再構築店舗"
            store.genre.value shouldBe "フレンチ"
            store.address.value shouldBe "大阪府大阪市"
            store.tel.value shouldBe "06-1234-5678"
            store.description.value shouldBe "再構築されたお店です。歴史があります。"
            store.open.value shouldBe "10:00-21:00"
            store.geoLocation.latitude shouldBe 34.6937
            store.geoLocation.longitude shouldBe 135.5023
        }
    }
})
