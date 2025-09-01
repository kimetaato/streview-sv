package com.streview.infrastructure.repository.stores

import com.github.michaelbull.result.getOrThrow
import com.streview.domain.commons.GeoLocation
import com.streview.domain.stores.Store
import com.streview.infrastructure.database.stores.StoreDatabaseDataSource
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import org.jetbrains.exposed.v1.r2dbc.transactions.suspendTransaction

class StoreRepositoryImplBasicTest : FreeSpec({

    val dbDataSource = StoreDatabaseDataSource()

    "StoreRepositoryImpl基本統合テスト" - {
        "データベースを通じた基本操作のテスト" - {
            "店舗の保存と取得が正しく動作すること" {
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

                    // 保存
                    val savedStore = dbDataSource.save(store).getOrThrow()

                    // 取得
                    val retrievedStore = dbDataSource.findByUUID(store.storeUUID).getOrThrow()

                    // 検証
                    retrievedStore.shouldNotBeNull()
                    retrievedStore.storeUUID shouldBe store.storeUUID
                    retrievedStore.name.value shouldBe "テスト店舗"
                    retrievedStore.genre.value shouldBe "和食"
                    retrievedStore.address.value shouldBe "東京都千代田区丸の内1-1-1"
                    retrievedStore.tel.value shouldBe "03-1234-5678"
                    retrievedStore.description.value shouldBe "おいしい和食レストラン"
                    retrievedStore.open.value shouldBe "11:00-22:00"
                    retrievedStore.geoLocation.latitude shouldBe 35.681236
                    retrievedStore.geoLocation.longitude shouldBe 139.767125

                    rollback()
                }
            }

            "距離順ソートが正しく動作すること" {
                suspendTransaction {
                    val baseLocation = GeoLocation.reconstruct(35.681236, 139.767125) // 東京駅

                    // 距離の異なる店舗を作成
                    val nearStore = Store.create(
                        name = "東京駅近くの店舗",
                        genre = "和食",
                        address = "東京都千代田区丸の内1-1-1",
                        tel = "0311111111",
                        description = "東京駅に最も近い美味しい和食店舗です",
                        open = "10:00-20:00",
                        latitude = 35.681236, // 東京駅
                        longitude = 139.767125
                    ).getOrThrow()

                    val farStore = Store.create(
                        name = "品川駅近くの店舗",
                        genre = "洋食",
                        address = "東京都品川区港南2-16-1",
                        tel = "0322222222",
                        description = "品川駅近くにある美味しい洋食レストランです",
                        open = "11:00-21:00",
                        latitude = 35.629026, // 品川駅
                        longitude = 139.738873
                    ).getOrThrow()

                    // 保存
                    dbDataSource.save(nearStore).getOrThrow()
                    dbDataSource.save(farStore).getOrThrow()

                    // 距離順ソート（遠い店舗を先にリストに入れて、近い順に並ぶかテスト）
                    val uuids = listOf(farStore.storeUUID, nearStore.storeUUID)
                    val sortedStores = dbDataSource.sortByDistanceInStoreUUIDs(uuids, baseLocation).getOrThrow()

                    // 近い順にソートされていることを確認
                    sortedStores.size shouldBe 2
                    sortedStores[0].storeUUID shouldBe nearStore.storeUUID // 最も近い
                    sortedStores[1].storeUUID shouldBe farStore.storeUUID // 遠い

                    rollback()
                }
            }
        }
    }
})
