package com.streview.infrastructure.database.users

import com.streview.domain.users.Profile
import com.streview.domain.users.User
import org.jetbrains.exposed.v1.core.ResultRow
import org.jetbrains.exposed.v1.core.statements.UpdateBuilder

/**
 * DBレコードからユーザードメインに変換する
 * @param row DBレコード
 */
fun toDomain(row: ResultRow): User =
    User.reconstruct(
        userID = row[UserTable.userID],
        catchMode = row[UserTable.catchMode],
        profile = Profile.reconstruct(
            name = row[UserTable.name],
            birthday = row[UserTable.birthday],
            gender = row[UserTable.gender],
            imageUUID = row[UserTable.iconUUID]
        ),
    )

/**
 * ユーザードメインをテーブルに変換する
 * @param user ドメイン
 */
fun toUserTable(user: User): (UpdateBuilder<*>) -> Unit {
    return {
        with(UserTable) {
            it[userID] = user.userID.value
            it[name] = user.profile.name.value
            it[birthday] = user.profile.birthday.value
            it[gender] = user.profile.gender.value
            it[iconUUID] = user.profile.imageUUID.value
            it[catchMode] = user.catchMode.value
        }
    }
}
