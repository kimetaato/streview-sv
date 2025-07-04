package com.streview.application.services

import com.streview.domain.commons.UserID
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe
import java.nio.charset.StandardCharsets
import java.util.Base64
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec

class EncounterDecryptionServiceTest: FreeSpec({
    
    val secretKey = "test-secret-key-1234567890123456" // 32バイト
    val service = EncounterDecryptionService(secretKey)
    
    // テスト用の暗号化メソッド
    fun encryptTestData(data: String): String {
        val key = SecretKeySpec(secretKey.toByteArray(StandardCharsets.UTF_8), "AES")
        val cipher = Cipher.getInstance("AES/ECB/PKCS5Padding")
        cipher.init(Cipher.ENCRYPT_MODE, key)
        val encryptedBytes = cipher.doFinal(data.toByteArray(StandardCharsets.UTF_8))
        return Base64.getEncoder().encodeToString(encryptedBytes)
    }
    
    "正常系" - {
        "有効な暗号化データからUserIDを抽出できる" {
            // Arrange
            val originalUserID = "abcd1234efgh5678ijkl9012mnop" // 28桁の有効なUserID
            val randomString = "randomdata123"
            val dataToEncrypt = originalUserID + randomString
            val encryptedData = encryptTestData(dataToEncrypt)
            
            // Act
            val extractedUserID = service.extractUserID(encryptedData)
            
            // Assert
            extractedUserID.value shouldBe originalUserID
        }
        
        "28桁のUserIDのみの場合も正常に抽出できる" {
            // Arrange
            val originalUserID = "testUserID123456789000000001" // 28桁の有効なUserID
            val encryptedData = encryptTestData(originalUserID)
            
            // Act
            val extractedUserID = service.extractUserID(encryptedData)
            
            // Assert
            extractedUserID.value shouldBe originalUserID
        }
    }
    
    "異常系" - {
        "不正な暗号化データの場合例外が発生する" {
            // Arrange
            val invalidEncryptedData = "invalid-encrypted-data"
            
            // Act & Assert
            shouldThrow<IllegalArgumentException> {
                service.extractUserID(invalidEncryptedData)
            }
        }
        
        "28桁未満のデータの場合例外が発生する" {
            // Arrange
            val shortData = "shortdata" // 28桁未満
            val encryptedData = encryptTestData(shortData)
            
            // Act & Assert
            shouldThrow<IllegalArgumentException> {
                service.extractUserID(encryptedData)
            }
        }
        
        "UserIDの形式が不正な場合例外が発生する" {
            // Arrange
            val invalidUserID = "invalid@user#id!123456789" // 28桁だが無効文字含む
            val randomString = "randomdata123"
            val dataToEncrypt = invalidUserID + randomString
            val encryptedData = encryptTestData(dataToEncrypt)
            
            // Act & Assert
            shouldThrow<IllegalArgumentException> {
                service.extractUserID(encryptedData)
            }
        }
    }
})