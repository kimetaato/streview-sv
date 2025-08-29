package com.streview.domain.stores.vo

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import com.streview.domain.commons.errors.InvalidFormatRules
import com.streview.domain.commons.errors.ValidationError

class Tel private constructor(
    val value: String
) {
    companion object {
        private val regex = (
            "^0\\d{1,4}-?\\d{1,4}-?\\d{4}$|" +
                "^0[789]0-?\\d{4}-?\\d{4}$|" +
                "^050-?\\d{4}-?\\d{4}$|" +
                "^0120-?\\d{3}-?\\d{3}$"
            ).toRegex()

        fun create(tel: String): Result<Tel, ValidationError> {
            val trimmedTel = tel.trim()
            return when {
                // 条件1: telが空または空白のみの場合
                trimmedTel.isBlank() -> Err(ValidationError.Required("tel"))
                // 条件2: telが正規表現に一致しない場合
                !trimmedTel.matches(regex) -> {
                    Err(ValidationError.InvalidFormat("tel", InvalidFormatRules.PATTERN_MISMATCH, tel))
                }
                // 上記のどの条件にも当てはまらない場合（＝バリデーションを通過した場合）
                else -> Ok(Tel(trimmedTel))
            }
        }
        fun reconstruct(tel: String): Tel {
            require(tel.isNotBlank()) { "tel cannot be blank" }
            require(tel.matches(regex)) { "tel format is invalid" }
            return Tel(tel)
        }
    }
}
