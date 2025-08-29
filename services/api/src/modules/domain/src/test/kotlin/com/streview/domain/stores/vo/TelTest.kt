package com.streview.domain.stores.vo

import com.streview.domain.commons.errors.InvalidFormatRules
import com.streview.domain.commons.errors.ValidationError
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import io.kotest.property.Arb
import io.kotest.property.arbitrary.Codepoint
import io.kotest.property.arbitrary.choice
import io.kotest.property.arbitrary.constant
import io.kotest.property.arbitrary.string
import io.kotest.property.arbitrary.whitespace
import io.kotest.property.checkAll

class TelTest : FreeSpec({
    "Telのvalue object" - {
        val validTelNumbers = listOf(
            "03-1234-5678", "0312345678", "06-9876-5432", "0698765432",
            "090-1234-5678", "09012345678", "080-1234-5678", "08012345678",
            "070-1234-5678", "07012345678", "050-1234-5678", "05012345678",
            "0120-123-456", "0120123456"
        )
        val validTelArb = Arb.choice(validTelNumbers.map { Arb.constant(it) })
        val whitespaceArb = Arb.string(range = 1..10, codepoints = Codepoint.whitespace())
        val invalidTelNumbers = listOf(
            "123-456-789", "03-abcd-5678", "1234567890", "abc-def-ghij",
            "03-12345-6789", "090-12345-6789", "0120-1234-567"
        )
        val invalidTelArb = Arb.choice(invalidTelNumbers.map { Arb.constant(it) })

        "正常系" - {
            "有効な電話番号で生成できる" - {
                checkAll(validTelArb) { tel: String ->
                    val result = Tel.create(tel)
                    result.isOk shouldBe true
                    result.value.value shouldBe tel
                }
            }

            "前後の空白は除去される" - {
                checkAll(
                    whitespaceArb,
                    validTelArb,
                    whitespaceArb,
                ) { prefix, tel, suffix ->
                    val result = Tel.create("$prefix$tel$suffix")

                    result.isOk shouldBe true
                    result.value.value shouldBe tel
                }
            }
        }

        "異常系" - {
            "空白文字のみの文字列で失敗" - {
                checkAll(whitespaceArb) { tel: String ->
                    val result = Tel.create(tel)
                    result.isErr shouldBe true
                    val error = result.error.shouldBeInstanceOf<ValidationError.Required>()
                    error.fieldName shouldBe "tel"
                }
            }

            "無効なパターンで失敗" - {
                checkAll(invalidTelArb) { tel: String ->
                    val result = Tel.create(tel)
                    result.isErr shouldBe true
                    val error = result.error.shouldBeInstanceOf<ValidationError.InvalidFormat>()
                    error.fieldName shouldBe "tel"
                    error.rule shouldBe InvalidFormatRules.PATTERN_MISMATCH
                }
            }
        }
    }
})
