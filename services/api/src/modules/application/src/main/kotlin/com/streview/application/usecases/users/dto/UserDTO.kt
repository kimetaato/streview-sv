package com.streview.usecase.users.dto


import com.streview.application.usecases.InputPort
import com.streview.application.usecases.OutputPort
import kotlinx.io.Source
import kotlinx.serialization.Serializable
import java.io.InputStream

@Serializable
data class GetUserRequest(val id: Int) : InputPort

@Serializable
data class GetUserResponse(val id: Int, val name: String, val email: String) : OutputPort

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

/**
 * ユーザの新規登録が完了した際のレスポンス
 * @param userID 登録したユーザーのID
 */
@Serializable
data class RegisterUserResponse(val userID: String) : OutputPort


data class UpdateProfileRequest(val name: String?, val image: InputStream) : InputPort

@Serializable
data class UpdateProfileResponse(val name: String, val birtDay: String, val gender: Int, val iconUrl: String) :
    OutputPort

