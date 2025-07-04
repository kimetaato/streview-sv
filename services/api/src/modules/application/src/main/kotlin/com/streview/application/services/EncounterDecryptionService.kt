package com.streview.application.services

import com.streview.domain.commons.UserID
import java.nio.charset.StandardCharsets
import java.util.Base64
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec

/**
 * encounterIDの暗号化/復号化を行うサービス
 * encounterIDは28桁のUserID + ランダム文字列を暗号化したもの
 */
open class EncounterDecryptionService(private val secretKey: String) {
    
    companion object {
        private const val ALGORITHM = "AES"
        private const val TRANSFORMATION = "AES/ECB/PKCS5Padding"
        private const val USER_ID_LENGTH = 28
    }
    
    /**
     * 暗号化されたencounterIDから28桁のUserIDを抽出する
     * @param encryptedEncounterID 暗号化されたencounterID
     * @return 28桁のUserID
     */
    open fun extractUserID(encryptedEncounterID: String): UserID {
        val decryptedData = decrypt(encryptedEncounterID)
        
        // 先頭28桁がUserID
        if (decryptedData.length < USER_ID_LENGTH) {
            throw IllegalArgumentException("復号化されたデータが不正です。28桁のUserIDが含まれていません。")
        }
        
        val userIdString = decryptedData.substring(0, USER_ID_LENGTH)
        return UserID(userIdString)
    }
    
    /**
     * 暗号化されたデータを復号化する
     * @param encryptedData Base64エンコードされた暗号化データ
     * @return 復号化された文字列
     */
    private fun decrypt(encryptedData: String): String {
        try {
            val key = SecretKeySpec(secretKey.toByteArray(StandardCharsets.UTF_8), ALGORITHM)
            val cipher = Cipher.getInstance(TRANSFORMATION)
            cipher.init(Cipher.DECRYPT_MODE, key)
            
            val decodedBytes = Base64.getDecoder().decode(encryptedData)
            val decryptedBytes = cipher.doFinal(decodedBytes)
            
            return String(decryptedBytes, StandardCharsets.UTF_8)
        } catch (e: Exception) {
            throw IllegalArgumentException("encounterIDの復号化に失敗しました", e)
        }
    }
}