package com.streview.application.usecases.relays.dto

import com.streview.application.usecases.InputPort
import com.streview.application.usecases.OutputPort
import kotlinx.serialization.Serializable

///**
// * 公開レビューを取得するリクエスト
// */
//@Serializable
//data class GetPublicReviewsRequest(
//    val userId: String
//) : InputPort
//
///**
// * 公開レビューを取得するレスポンス
// */
//@Serializable
//data class GetPublicReviewsResponse(
//    val reviewIds: List<String>
//) : OutputPort

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
