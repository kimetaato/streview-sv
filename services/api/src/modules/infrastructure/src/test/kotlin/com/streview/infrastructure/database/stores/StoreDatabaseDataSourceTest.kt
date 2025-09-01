package com.streview.infrastructure.database.stores

import com.github.michaelbull.result.getOrThrow
import com.streview.domain.commons.GeoLocation
import com.streview.domain.stores.Store
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.jetbrains.exposed.v1.r2dbc.transactions.suspendTransaction

class StoreDatabaseDataSourceTest : FreeSpec({

    val dataSource = StoreDatabaseDataSource()

    "StoreDatabaseDataSourceの統合テスト" - {
        "saveメソッドのテスト" - {
            "店舗を保存できること" {
                suspendTransaction {
                    val store = Store.create(
                        name = "テスト店舗",
                        genre = "和食",
                        address = "東京都千代田区丸の内1-1-1",
                        tel = "03-1234-5678",
                        description = "おいしい和食レストラン",
                        open = "11:00-22:00",
                        latitude = 35.681236,
                        longitude = 139.767125
                    ).getOrThrow()

                    val savedStore = dataSource.save(store).getOrThrow()

                    savedStore.storeUUID shouldBe store.storeUUID
                    savedStore.name.value shouldBe "テスト店舗"
                    savedStore.genre.value shouldBe "和食"
                    savedStore.address.value shouldBe "東京都千代田区丸の内1-1-1"
                    savedStore.tel.value shouldBe "03-1234-5678"
                    savedStore.description.value shouldBe "おいしい和食レストラン"
                    savedStore.open.value shouldBe "11:00-22:00"
                    savedStore.geoLocation.latitude shouldBe 35.681236
                    savedStore.geoLocation.longitude shouldBe 139.767125

                    rollback()
                }
            }
        }

        "findByUUIDメソッドのテスト" - {
            "保存した店舗を取得できること" {
                suspendTransaction {
                    val originalStore = Store.create(
                        name = "取得テスト店舗",
                        genre = "洋食",
                        address = "東京都渋谷区神南1-1-1",
                        tel = "03-9876-5432",
                        description = "美味しい洋食レストラン",
                        open = "12:00-23:00",
                        latitude = 35.658034,
                        longitude = 139.758148
                    ).getOrThrow()

                    dataSource.save(originalStore).getOrThrow()

                    val foundStore = dataSource.findByUUID(originalStore.storeUUID).getOrThrow()

                    foundStore shouldNotBe null
                    foundStore.shouldNotBeNull()
                    foundStore.storeUUID shouldBe originalStore.storeUUID
                    foundStore.name.value shouldBe "取得テスト店舗"
                    foundStore.genre.value shouldBe "洋食"
                    foundStore.address.value shouldBe "東京都渋谷区神南1-1-1"
                    foundStore.tel.value shouldBe "03-9876-5432"
                    foundStore.description.value shouldBe "美味しい洋食レストラン"
                    foundStore.open.value shouldBe "12:00-23:00"
                    foundStore.geoLocation.latitude shouldBe 35.658034
                    foundStore.geoLocation.longitude shouldBe 139.758148
                    rollback()
                }
            }
        }

        "sortByDistanceInUUIDsメソッドのテスト" - {
            "距離順にソートされた店舗リストを取得できること" {
                suspendTransaction {
                    // 基準点
                    val baseLocation = GeoLocation.reconstruct(35.681236, 139.767125) // 東京駅

                    // 距離の異なる3つの店舗を作成
                    val store1 = Store.create(
                        name = "東京駅前店舗",
                        genre = "和食",
                        address = "東京都千代田区丸の内1-1-1",
                        tel = "0311111111",
                        description = "東京駅前にある伝統的な和食レストランです",
                        open = "10:00-20:00",
                        latitude = 35.681236, // 東京駅（距離0km）
                        longitude = 139.767125
                    ).getOrThrow()

                    val store2 = Store.create(
                        name = "新橋店舗",
                        genre = "洋食",
                        address = "東京都港区新橋1-1-1",
                        tel = "0322222222",
                        description = "新橋にあるモダンな洋食レストランです",
                        open = "11:00-21:00",
                        latitude = 35.658034, // 新橋駅（約3km）
                        longitude = 139.758148
                    ).getOrThrow()

                    val store3 = Store.create(
                        name = "品川店舗",
                        genre = "中華",
                        address = "東京都品川区港南2-16-1",
                        tel = "0333333333",
                        description = "品川にある本格的な中華料理レストランです",
                        open = "12:00-22:00",
                        latitude = 35.629026, // 品川駅（約7km）
                        longitude = 139.738873
                    ).getOrThrow()

                    // 店舗を保存
                    dataSource.save(store1).getOrThrow()
                    dataSource.save(store2).getOrThrow()
                    dataSource.save(store3).getOrThrow()

                    // UUIDリストを逆順で渡して、距離順にソートされることを確認
                    val uuids = listOf(store3.storeUUID, store2.storeUUID, store1.storeUUID)
                    val sortedStores = dataSource.sortByDistanceInStoreUUIDs(uuids, baseLocation).getOrThrow()

                    // 距離順（近い順）にソートされていることを確認
                    sortedStores.size shouldBe 3
                    sortedStores[0].storeUUID shouldBe store1.storeUUID // 最も近い
                    sortedStores[1].storeUUID shouldBe store2.storeUUID // 2番目
                    sortedStores[2].storeUUID shouldBe store3.storeUUID // 最も遠い

                    rollback()
                }
            }
        }

        "save-findサイクルのテスト" - {
            "保存と取得の整合性が保たれること" {
                suspendTransaction {
                    val originalStore = Store.create(
                        name = "整合性テスト店舗",
                        genre = "イタリアン",
                        address = "東京都港区六本木1-1-1",
                        tel = "0344444444",
                        description = "六本木にある本格的なイタリアンレストランです",
                        open = "17:00-24:00",
                        latitude = 35.662832,
                        longitude = 139.731781
                    ).getOrThrow()

                    val savedStore = dataSource.save(originalStore).getOrThrow()
                    val retrievedStore = dataSource.findByUUID(originalStore.storeUUID).getOrThrow()

                    retrievedStore.shouldNotBeNull()
                    savedStore.storeUUID shouldBe retrievedStore.storeUUID
                    savedStore.name.value shouldBe retrievedStore.name.value
                    savedStore.genre.value shouldBe retrievedStore.genre.value
                    savedStore.address.value shouldBe retrievedStore.address.value
                    savedStore.tel.value shouldBe retrievedStore.tel.value
                    savedStore.description.value shouldBe retrievedStore.description.value
                    savedStore.open.value shouldBe retrievedStore.open.value
                    savedStore.geoLocation.latitude shouldBe retrievedStore.geoLocation.latitude
                    savedStore.geoLocation.longitude shouldBe retrievedStore.geoLocation.longitude

                    rollback()
                }
            }
        }
    }
})
