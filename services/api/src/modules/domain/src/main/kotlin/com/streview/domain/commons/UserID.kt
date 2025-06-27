package com.streview.domain.commons


/**
 * firebase authentication により発行されるユーザー識別の28桁の文字列
 */
data class UserID(val value: String) {
    companion object {
        private val regex = Regex("^[a-zA-Z0-9]{28}$")
    }

    init {
        require(value.matches(regex)) { "不正な形式です。" }
    }
}
