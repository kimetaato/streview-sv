package com.streview.domain.users

data class User(
    val id: UserId,
    val name: UserName,
    val email: Email
) {
    companion object {
        fun create(id: Int, name: String, email: String): User {
            return User(
                id = UserId(id),
                name = UserName(name),
                email = Email(email)
            )
        }
    }
}

// 値オブジェクトの導入
@JvmInline
value class UserId(val value: Int) {
    init {
        require(value > 0) { "User ID must be positive" }
    }
}

@JvmInline
value class UserName(val value: String) {
    init {
        require(value.isNotEmpty()) { "User name cannot be empty" }
    }
}

@JvmInline
value class Email(val value: String) {
    init {
        require(value.contains("@")) { "Invalid email format" }
    }
}