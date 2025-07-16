package com.streview.application.services

import com.streview.domain.commons.UserID
import java.nio.charset.StandardCharsets
import java.security.KeyFactory
import java.security.PrivateKey
import java.security.spec.PKCS8EncodedKeySpec
import java.util.*
import javax.crypto.Cipher


/**
 * 初期化処理に必要なパラメータを取得。トリムし、base64のデコードをして、秘密鍵として処理をする
 * @param pemKey pem形式の秘密鍵
 */
class EncounterDecryptionConfig(pemKey: String) {
    companion object {
        private const val ALGORITHM = "RSA"
    }

    private val key: String = pemKey
        .replace("-----BEGIN PRIVATE KEY-----", "")
        .replace("-----END PRIVATE KEY-----", "")
        .replace("-----BEGIN RSA PRIVATE KEY-----", "")
        .replace("-----END RSA PRIVATE KEY-----", "")
        .replace("\\r\\n".toRegex(), "") // Windows改行
        .replace("\\n".toRegex(), "")     // Unix改行
        .replace("\\r".toRegex(), "")     // Mac改行
        .replace(" ", "")                 // スペース
        .replace("\\t".toRegex(), "")     // タブ
        .trim()

    // Base64エンコードされた秘密鍵をデコード
    private val keyBytes = Base64.getDecoder().decode(key)
    private val keySpec = PKCS8EncodedKeySpec(keyBytes)
    private val keyFactory = KeyFactory.getInstance(ALGORITHM)
    val privateKey: PrivateKey = keyFactory.generatePrivate(keySpec)
}

/**
 * encounterIDの暗号化/復号化を行うサービス
 * encounterIDは28桁のUserID + ランダム文字列を暗号化したもの
 */
open class EncounterDecryptionService(config: EncounterDecryptionConfig) {

    companion object {
        private const val TRANSFORMATION = "RSA/ECB/OAEPWithSHA-256AndMGF1Padding"
        private const val USER_ID_LENGTH = 28
    }

    // 複合に使うインスタンスの初期化
    val cipher: Cipher = Cipher.getInstance(TRANSFORMATION)

    init {
        cipher.init(Cipher.DECRYPT_MODE, config.privateKey)
    }

    /**
     * 暗号化されたencounterIDから28桁のUserIDを抽出する
     * @param encryptedEncounterID 暗号化されたencounterID
     * @return 28桁のUserID
     */
    open fun extractUserID(encryptedEncounterID: String): UserID {
        val decryptedData = decrypt(encryptedEncounterID)

        // 先頭28桁がUserID
        when {
            decryptedData.length < USER_ID_LENGTH -> {
                throw IllegalArgumentException("復号化されたデータが不正です。28桁のUserIDが含まれていません。")
            }

            else -> {
                val userIdString = decryptedData.substring(0, USER_ID_LENGTH)
                return UserID(userIdString)
            }
        }
    }

    /**
     * 暗号化されたデータを復号化する
     * @param encryptedData Base64エンコードされた暗号化データ
     * @return 復号化された文字列
     */
    private fun decrypt(encryptedData: String): String {
        try {
            // 暗号化されたデータをデコードして復号化
            val decodedBytes = Base64.getDecoder().decode(encryptedData)
            val decryptedBytes = cipher.doFinal(decodedBytes)

            return String(decryptedBytes, StandardCharsets.UTF_8)
        } catch (e: Exception) {
            throw IllegalArgumentException("encounterIDの復号化に失敗しました", e)
        }
    }
}