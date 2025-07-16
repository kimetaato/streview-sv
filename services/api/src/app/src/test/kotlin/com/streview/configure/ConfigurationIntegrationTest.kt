package com.streview.configure

import com.streview.application.services.EncounterDecryptionConfig
import com.streview.application.services.EncounterDecryptionService
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldNotBe
import io.ktor.server.testing.*
import kotlinx.io.buffered
import kotlinx.io.files.Path
import kotlinx.io.files.SystemFileSystem
import kotlinx.io.readString
import org.koin.core.context.GlobalContext
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.koin.test.KoinTest
import org.koin.test.inject
import java.security.KeyPairGenerator
import java.util.*
import kotlin.io.path.createTempFile
import kotlin.io.path.deleteIfExists
import kotlin.io.path.writeText

class ConfigurationIntegrationTest : FreeSpec(), KoinTest {

    // テスト用の秘密鍵生成
    private val keyPairGenerator = KeyPairGenerator.getInstance("RSA").apply { initialize(4096) }
    private val testKeyPair = keyPairGenerator.generateKeyPair()
    private val testPrivateKeyPem = generatePemFormattedPrivateKey(testKeyPair.private.encoded)

    private fun generatePemFormattedPrivateKey(keyBytes: ByteArray): String {
        val base64Key = Base64.getEncoder().encodeToString(keyBytes)
        return "-----BEGIN PRIVATE KEY-----\n" +
                base64Key.chunked(64).joinToString("\n") + "\n" +
                "-----END PRIVATE KEY-----"
    }

    init {
        // テスト前後でKoinの状態を管理
        beforeEach {
            if (GlobalContext.getOrNull() != null) {
                stopKoin()
            }
        }

        afterEach {
            if (GlobalContext.getOrNull() != null) {
                stopKoin()
            }
        }

        "設定ファイルからの秘密鍵読み込み統合テスト" - {

            "正常系: 有効な秘密鍵ファイルから設定を読み込める" {
                // 準備: テスト用秘密鍵ファイルを作成
                val tempKeyFile = createTempFile(suffix = ".pem")
                tempKeyFile.writeText(testPrivateKeyPem)
                try {
                    // 準備: 設定値を直接注入するKoinモジュールを開始
                    testApplication {
                        startKoin {
                            modules(module {
                                single<EncounterDecryptionConfig> {
                                    val item =
                                        SystemFileSystem.source(Path(tempKeyFile.toString())).buffered().readString()

                                    EncounterDecryptionConfig(
                                        item
                                    )
                                }
                                single<EncounterDecryptionService> {
                                    EncounterDecryptionService(get())
                                }
                            })
                        }
                    }

                    // 実行: DIコンテナから設定を取得
                    val config: EncounterDecryptionConfig by inject()
                    val service: EncounterDecryptionService by inject()

                    // 検証: 設定が正常に読み込まれることを確認
                    config.privateKey shouldNotBe null

                    // 検証: サービスが正常に初期化されることを確認
                    service shouldNotBe null
                } finally {
                    // 後片付け: 一時ファイルを削除
                    tempKeyFile.deleteIfExists()
                }
            }

            "異常系: 存在しないファイルパスで例外が発生" {
                // 準備: 存在しないファイルパスを設定
                val nonExistentPath = "/non/existent/path/private.key"

                // 準備: 存在しないファイルパスを使用するKoinモジュールを開始
                startKoin {
                    modules(module {
                        single<EncounterDecryptionConfig> {
                            val item = SystemFileSystem.source(Path(nonExistentPath)).buffered().readString()

                            EncounterDecryptionConfig(
                                item
                            )
                        }
                    })
                }
                // 検証: 存在しないファイルパスでDI取得時に例外が発生することを確認
                shouldThrow<Exception> {
                    val config: EncounterDecryptionConfig by inject()
                    config.privateKey // 遅延評価のため、実際にアクセスして例外を発生させる
                }
            }

            "異常系: 不正な秘密鍵フォーマットで例外が発生" {
                // 準備: 不正な秘密鍵内容でファイルを作成
                val invalidKeyContent = "invalid-private-key-content"
                val tempKeyFile = createTempFile(suffix = ".pem")
                tempKeyFile.writeText(invalidKeyContent)

                try {
                    startKoin {
                        modules(module {
                            single<EncounterDecryptionConfig> {
                                val item = SystemFileSystem.source(Path(tempKeyFile.toString())).buffered().readString()

                                EncounterDecryptionConfig(
                                    item
                                )
                            }
                            single<EncounterDecryptionService> {
                                EncounterDecryptionService(get())
                            }
                        })
                    }

                    // 検証: 不正な秘密鍵でサービス初期化時に例外が発生することを確認
                    shouldThrow<Exception> {
                        val service: EncounterDecryptionService by inject()
                        service.toString() // 遅延評価のため、実際にアクセスして例外を発生させる
                    }
                } finally {
                    // 後片付け: 一時ファイルを削除
                    tempKeyFile.deleteIfExists()
                }
            }
        }


        "空ファイルの場合の例外処理確認" {
            // 準備: 空のテスト用ファイルを作成
            val emptyKeyFile = createTempFile(suffix = ".pem")
            emptyKeyFile.writeText("")

            try {
                // 検証: 空ファイルでサービス初期化時に例外が発生することを確認
                shouldThrow<Exception> {
                    val config = EncounterDecryptionConfig(
                        SystemFileSystem.source(Path(emptyKeyFile.toString())).buffered().readString()
                    )
                    EncounterDecryptionService(config)
                }
            } finally {
                // 後片付け: 一時ファイルを削除
                emptyKeyFile.deleteIfExists()
            }
        }
    }
}