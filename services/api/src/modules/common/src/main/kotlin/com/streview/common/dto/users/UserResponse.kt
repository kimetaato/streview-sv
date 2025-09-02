package com.streview.common.dto.users

import com.streview.common.dto.OutputPort
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * ユーザの新規登録が完了した際のレスポンス
 * @param userID 登録したユーザーのID
 */
@Serializable
data class RegisterUserResponse(
    @SerialName("user_id") val userID: String
) : OutputPort

@Serializable
data class UpdateProfileResponse(
    @SerialName("user_id") val userID: String
) : OutputPort
