package com.streview.infrastructure.database.users

import com.streview.domain.exceptions.DataFormatException
import com.streview.domain.users.Profile
import com.streview.domain.users.User
import com.streview.infrastructure.database.models.UsersTable
import org.jetbrains.exposed.v1.core.ResultRow
import org.jetbrains.exposed.v1.core.statements.UpdateBuilder


/**
 * DBレコードからユーザードメインに変換する
 * @param row DBレコード
 */
fun UserRepositoryImpl.toDomain(row: ResultRow): User {
    // プロフィールを作成
    val profile: Profile = try {
        Profile.create(
            row[UsersTable.name],
            row[UsersTable.birthday],
            row[UsersTable.gender],
            row[UsersTable.iconUUID]
        )
    } catch (e: Exception) {
        throw DataFormatException("ユーザープロフィールが取得できませんでした。", e)
    }

    // ユーザードメインに変換する
    return User.create(
        row[UsersTable.id],
        profile,
        row[UsersTable.catchMode],
    )
}


/**
 * ユーザードメインをテーブルに変換する
 * @param user ドメイン
 */
fun UserRepositoryImpl.toUserTable(user: User): (UpdateBuilder<*>) -> Unit {
    return {
        with(UsersTable) {
            it[id] = user.userID.value
            it[name] = user.profile.name.value
            it[birthday] = user.profile.birthday.value
            it[gender] = user.profile.gender.value
            it[iconUUID] = user.profile.imageUUID.value
            it[catchMode] = user.catchMode.value
        }
    }
}
