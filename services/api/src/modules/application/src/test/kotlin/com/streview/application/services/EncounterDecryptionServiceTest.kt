package com.streview.application.services

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.stringPattern
import io.kotest.property.checkAll
import java.nio.charset.StandardCharsets
import java.security.KeyPairGenerator
import java.util.*
import javax.crypto.Cipher

class EncounterDecryptionServiceTest : FreeSpec({

    // RSA鍵ペア生成
    val keyPairGenerator = KeyPairGenerator.getInstance("RSA")
    keyPairGenerator.initialize(4096)
    val testKeyPair = keyPairGenerator.generateKeyPair()

    // 秘密鍵をBase64エンコード
    val encodedPrivateKey = Base64.getEncoder().encodeToString(testKeyPair.private.encoded)
    val config = EncounterDecryptionConfig(encodedPrivateKey)
    val service = EncounterDecryptionService(config)

    // Arbジェネレータ定義
    val validUserIDArb = Arb.stringPattern("[a-zA-Z0-9]{28}")
    val randomStringArb = Arb.stringPattern("[a-zA-Z0-9]{0,50}")
    val shortStringArb = Arb.stringPattern("[a-zA-Z0-9]{0,27}")

    // テスト用暗号化メソッド
    fun encryptTestData(data: String): String {
        val cipher = Cipher.getInstance("RSA/ECB/OAEPWithSHA-256AndMGF1Padding")
        cipher.init(Cipher.ENCRYPT_MODE, testKeyPair.public)
        val encryptedBytes = cipher.doFinal(data.toByteArray(StandardCharsets.UTF_8))
        return Base64.getEncoder().encodeToString(encryptedBytes)
    }

    // 不正な暗号化データ生成
    fun generateInvalidEncryptedData(): String {
        return Base64.getEncoder().encodeToString("invalid-data".toByteArray())
    }

    "正常系" - {
        "プロパティテスト: 有効な暗号化データからUserIDを抽出できる" {
            checkAll(validUserIDArb, randomStringArb) { userID, randomString ->
                // 準備: テストデータの作成
                val dataToEncrypt = userID + randomString
                val encryptedData = encryptTestData(dataToEncrypt)

                // 実行: UserIDの抽出
                val extractedUserID = service.extractUserID(encryptedData)

                // 検証: 抽出されたUserIDが正しいことを確認
                extractedUserID.value shouldBe userID
            }
        }
    }

    "異常系" - {
        "プロパティテスト: 不正なBase64データで例外が発生する" {
            val invalidBase64Arb = Arb.stringPattern("[a-zA-Z0-9]{1,100}")

            checkAll(invalidBase64Arb) { invalidData ->
                // 検証: 不正なBase64データで例外が発生することを確認
                shouldThrow<IllegalArgumentException> {
                    service.extractUserID(invalidData)
                }
            }
        }

        "プロパティテスト: 28桁未満のデータで例外が発生する" {
            checkAll(shortStringArb) { shortData ->
                // 準備: 28桁未満のデータを暗号化
                val encryptedData = encryptTestData(shortData)

                // 検証: 28桁未満のデータで例外が発生することを確認
                shouldThrow<IllegalArgumentException> {
                    service.extractUserID(encryptedData)
                }
            }
        }

        "具体的なテストケース: 完全に不正な暗号化データ" {
            // 準備: 完全に不正な暗号化データを生成
            val invalidEncryptedData = generateInvalidEncryptedData()

            // 検証: 不正な暗号化データで例外が発生することを確認
            shouldThrow<IllegalArgumentException> {
                service.extractUserID(invalidEncryptedData)
            }
        }

        "具体的なテストケース: 28桁未満のデータ" {
            // 準備: 28桁未満の具体的なデータを作成
            val shortData = "shortdata"
            val encryptedData = encryptTestData(shortData)

            // 検証: 28桁未満のデータで例外が発生することを確認
            shouldThrow<IllegalArgumentException> {
                service.extractUserID(encryptedData)
            }
        }
    }

    "セキュリティテスト" - {
        "異なる秘密鍵では復号化できない" {
            // 準備: 別の鍵ペアを生成
            val anotherKeyPair = keyPairGenerator.generateKeyPair()
            val anotherEncodedPrivateKey = Base64.getEncoder().encodeToString(anotherKeyPair.private.encoded)
            val anotherConfig = EncounterDecryptionConfig(anotherEncodedPrivateKey)
            val anotherService = EncounterDecryptionService(anotherConfig)

            // 準備: 元の公開鍵でテストデータを暗号化
            val testUserID = "abcdefghijklmnopqrstuvwxyz12"
            val encryptedData = encryptTestData(testUserID)

            // 検証: 異なる秘密鍵では復号化できず例外が発生することを確認
            shouldThrow<IllegalArgumentException> {
                anotherService.extractUserID(encryptedData)
            }
        }

        "不正な秘密鍵フォーマットで初期化エラー" {
            // 準備: 不正な秘密鍵フォーマットの設定を作成
            val invalidPrivateKey = "invalid-private-key"

            // 検証: 不正な秘密鍵で初期化時に例外が発生することを確認
            shouldThrow<IllegalArgumentException> {
                EncounterDecryptionConfig(invalidPrivateKey)
            }
        }
    }
})