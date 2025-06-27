package com.streview.domain.users

import com.streview.domain.exceptions.ValidationError
import com.streview.domain.exceptions.ValidationException
import com.streview.domain.exceptions.addError
import com.streview.domain.images.ImageUUID
import kotlinx.datetime.*


data class Profile(
    val name: Name,
    val birthday: Birthday,
    val gender: Gender,
    val imageUUID: ImageUUID,
) {
    companion object {
        fun create(
            name: String,
            birthday: LocalDate,
            gender: String,
            imageUUID: String
        ): Profile {

            //エラーを収集するリスト
            val errors: MutableList<ValidationError> = mutableListOf()

            // 名前オブジェクトの生成
            val name: Name? = try {
                Name(name)
            } catch (e: IllegalArgumentException) {
                errors.addError("name", e.message)
                null
            }

            // 生年月日の生成
            val birthDay: Birthday? = try {
                Birthday(birthday)
            } catch (e: IllegalArgumentException) {
                errors.addError("birthDay", e.message)
                null
            }

            // 性別の設定
            val gender: Gender? = try {
                Gender.fromCode(gender)
            } catch (e: IllegalArgumentException) {
                errors.addError("gender", e.message)
                null
            }

            // アイコンのパスの設定
            val imageUUID: ImageUUID? = try {
                ImageUUID(imageUUID)
            } catch (e: IllegalArgumentException) {
                errors.addError("imageUUID", e.message)
                null
            }

            // エラーの有無によってレスポンスを分岐
            return if (errors.isEmpty()) {
                Profile(name!!, birthDay!!, gender!!, imageUUID!!)
            } else {
                throw ValidationException("入力内容に不備があります。", errors)
            }
        }
    }
}

@JvmInline
value class Name(val value: String) {
    companion object {
        private const val MIN_LENGTH = 3
        private const val MAX_LENGTH = 64
    }

    init {
        require(value.isNotEmpty()) { "名前は入力する必要があります。" }
        require(value.length >= MIN_LENGTH) { "名前は${MIN_LENGTH}文字以上にしてください。" }
        require(value.length <= MAX_LENGTH) { "名前は${MAX_LENGTH}文字以下にしてください。" }
    }
}

@JvmInline
value class Birthday(val value: LocalDate) {
    companion object {
        private const val MIN_AGE = 13
        private const val MAX_AGE = 150
    }

    init {
        val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
        require(value <= today) { "不正な形式です。" }
        val age = value.yearsUntil(today)
        require(age >= MIN_AGE) { "${MIN_AGE}歳未満の方はご利用いただけません。" }
        require(age < MAX_AGE) { "誕生日が古すぎます。" }
    }
}

enum class Gender(val value: String) {
    Male("male"), Female("female"), Unknown("unknown");

    companion object {
        private val map = Gender.values().associateBy(Gender::value)
        fun fromCode(value: String): Gender {
            return map[value.lowercase()]
                ?: throw IllegalArgumentException("不正な形式です。")
        }
    }
}





