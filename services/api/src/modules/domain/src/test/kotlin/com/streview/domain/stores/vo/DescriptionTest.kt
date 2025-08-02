package com.streview.domain.stores.vo

import com.streview.domain.commons.errors.InvalidFormatRules
import com.streview.domain.commons.errors.ValidationError
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import io.kotest.property.Arb
import io.kotest.property.arbitrary.Codepoint
import io.kotest.property.arbitrary.arbitrary
import io.kotest.property.arbitrary.char
import io.kotest.property.arbitrary.next
import io.kotest.property.arbitrary.string
import io.kotest.property.arbitrary.whitespace
import io.kotest.property.checkAll

class DescriptionTest : FreeSpec({
    "Descriptionのvalue object" - {
        fun descriptionArb(range: IntRange): Arb<String> {
            return arbitrary { rs ->
                val length = range.random(rs.random)
                when (length) {
                    1 -> Arb.char(ranges = listOf(CharRange('a', 'z'))).next(rs).toString()
                    else -> {
                        val prefix = Arb.char(ranges = listOf(CharRange('a', 'z'))).next(rs)
                        val middle = Arb.string(range = length - 2..length - 2).next(rs)
                        val suffix = Arb.char(ranges = listOf(CharRange('a', 'z'))).next(rs)
                        "$prefix$middle$suffix"
                    }
                }
            }
        }
        val whitespaceArb = Arb.string(range = 1..10, codepoints = Codepoint.whitespace())

        "正常系" - {
            "規定範囲内の長さで生成できる" - {
                checkAll(descriptionArb(10..1024)) { description: String ->
                    val result = Description.create(description)
                    result.isOk shouldBe true
                    result.value.value shouldBe description
                }
            }

            "前後の空白は除去される" - {
                checkAll(
                    whitespaceArb,
                    descriptionArb(10..1024),
                    whitespaceArb,
                ) { prefix, description, suffix ->
                    val result = Description.create("$prefix$description$suffix")

                    result.isOk shouldBe true
                    result.value.value shouldBe description
                }
            }
        }

        "異常系" - {
            "空白文字のみの文字列で失敗" - {
                checkAll(whitespaceArb) { description: String ->
                    val result = Description.create(description)
                    result.isErr shouldBe true
                    val error = result.error.shouldBeInstanceOf<ValidationError.Required>()
                    error.fieldName shouldBe "description"
                }
            }

            "短すぎる説明で失敗" - {
                checkAll(descriptionArb(1..9)) { description: String ->
                    val result = Description.create(description)
                    result.isErr shouldBe true
                    val error = result.error.shouldBeInstanceOf<ValidationError.InvalidFormat>()
                    error.fieldName shouldBe "description"
                    error.rule shouldBe InvalidFormatRules.TOO_SHORT
                }
            }

            "長すぎる説明で失敗" - {
                checkAll(descriptionArb(1025..1200)) { description: String ->
                    val result = Description.create(description)
                    result.isErr shouldBe true
                    val error = result.error.shouldBeInstanceOf<ValidationError.InvalidFormat>()
                    error.fieldName shouldBe "description"
                    error.rule shouldBe InvalidFormatRules.TOO_LONG
                }
            }
        }
    }
})
