package com.streview.application.usecases.relays.dto

import com.streview.application.usecases.InputPort
import com.streview.application.usecases.OutputPort
import kotlinx.serialization.Serializable

/**
 * 特定レビューを再共有状態(ReReview)に設定するリクエスト
 */
@Serializable
data class RelayStatusToggleRequest(
    val userID: String,
    val reviewUUID: String,
    val toggleStatus: Boolean
) : InputPort

/**
 * 特定レビューを再共有状態(ReReview)に設定するリクエストのレスポンス
 */
@Serializable
data class RelayStatusToggleResponse(
    val reviewUUID: String
) : OutputPort
