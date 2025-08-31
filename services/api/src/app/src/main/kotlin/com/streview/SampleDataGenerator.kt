package com.streview

import com.github.michaelbull.result.fold
import com.streview.application.services.EncounterDecryptionConfig
import com.streview.application.services.ImageStorageConfig
import com.streview.configure.configureDatabase
import com.streview.configure.dependency.application.applicationServiceModule
import com.streview.configure.dependency.application.useCaseModule
import com.streview.configure.dependency.domain.domainServiceModule
import com.streview.configure.dependency.domain.eventModule
import com.streview.configure.dependency.domain.repositoryModule
import com.streview.configure.httpClientModule
import com.streview.domain.commons.GeoLocation
import com.streview.domain.stores.StoreRepository
import kotlinx.coroutines.runBlocking
import org.jetbrains.exposed.v1.r2dbc.transactions.suspendTransaction
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.context.startKoin
import org.koin.dsl.module

class SampleDataGenerator : KoinComponent {
    private val storeRepository: StoreRepository by inject()

    suspend fun generateSampleData() {
        println("サンプルデータの生成を開始します...")

        // 東京駅の座標でテスト
        val tokyoStation = GeoLocation.reconstruct(35.6812, 139.7671)
        println("東京駅周辺で店舗検索中...")

        storeRepository.searchFromGeoLocation(tokyoStation).fold(
            success = { stores ->
                println("${stores.size} 件の店舗が見つかりました")
                processStores(stores)
            },
            failure = { error ->
                println("店舗検索に失敗しました: $error")
            }
        )
        println("サンプルデータの生成が完了しました")
    }

    private suspend fun processStores(stores: List<com.streview.domain.stores.Store>) {
        var savedCount = 0
        suspendTransaction {
            stores.forEach { store ->
                storeRepository.save(store).fold(
                    success = {
                        println("店舗「${store.name.value}」を保存しました")
                        savedCount++
                    },
                    failure = { error ->
                        println("店舗「${store.name.value}」の保存に失敗: $error")
                    }
                )
            }
        }
        println("合計 $savedCount 件の店舗を保存しました")
    }
}

// スタンドアローン実行用の設定モジュール
val sampleDataConfigModule = module {
    single<ImageStorageConfig> {
        ImageStorageConfig()
    }
    single<EncounterDecryptionConfig> {
        EncounterDecryptionConfig("dummy-secret-key")
    }
}

fun main() = runBlocking {
    println("サンプルデータ生成ツールを開始します...")

    // データベース接続の初期化
    configureDatabase()
    println("データベース接続を初期化しました")

    // Koinの初期化
    startKoin {
        modules(
            useCaseModule,
            applicationServiceModule,
            domainServiceModule,
            repositoryModule,
            httpClientModule,
            sampleDataConfigModule,
            eventModule
        )
    }

    try {
        val generator = SampleDataGenerator()
        generator.generateSampleData()
        println("サンプルデータ生成ツールが正常に終了しました")
    } catch (e: Exception) {
        println("エラーが発生しました: ${e.message}")
        kotlin.system.exitProcess(1)
    }
}
