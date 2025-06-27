package com.streview.domain.users

import com.streview.domain.commons.UserID


data class User(
    val userID: UserID,
    val profile: Profile,
    val catchMode: CatchMode,
) {
    companion object {
        // プロフィールとユーザーIDで新規サクせう
        fun create(userID: UserID, profile: Profile): User {
            return User(
                userID = userID,
                profile = profile,
                catchMode = CatchMode.Area,
            )
        }

        // DBから取得したときの再生成
        fun create(userUuid: String, profile: Profile, catchMode: String): User {
            return User(
                userID = UserID(userUuid),
                profile = profile,
                catchMode = CatchMode.fromCode(catchMode.lowercase()),
            )
        }
    }
}

/**
 * ユーザーが飲食店のレビューを取得する際に何を優先するかを設定するパラメータ
 *
 */
enum class CatchMode(val value: String) {
    Area("area"), Recommend("recommend"), NearBy("nearby");

    companion object {
        private val map = CatchMode.values().associateBy(CatchMode::value)

        fun fromCode(value: String): CatchMode {
            return map[value.lowercase()]
                ?: throw IllegalArgumentException("無効なCatchModeコード: $value")
        }
    }
}

