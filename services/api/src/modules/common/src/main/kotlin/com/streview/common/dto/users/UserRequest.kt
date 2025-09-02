package com.streview.common.dto.users

import com.streview.common.dto.InputPort
import kotlinx.io.Source
import java.io.InputStream

/**
 * ユーザーの新規登録の際に必要なパラメータ
 * @param name ユーザーのプロフィール名
 * @param birtDay 生年月日
 * @param gender 性別
 * @param imageSource 画像ストリーム
 */
data class RegisterUserRequest(
    val userID: String,
    val name: String,
    val birtDay: String,
    val gender: String,
    val imageSource: Source
) : InputPort

data class UpdateProfileRequest(
    val name: String?,
    val image: InputStream
) : InputPort
