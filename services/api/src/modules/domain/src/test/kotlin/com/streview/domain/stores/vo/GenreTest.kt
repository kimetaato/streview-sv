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

class GenreTest : FreeSpec({
    "Genreのvalue object" - {
        fun genreArb(range: IntRange): Arb<String> {
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
                checkAll(genreArb(1..15)) { genre: String ->
                    val result = Genre.create(genre)
                    result.isOk shouldBe true
                    result.value.value shouldBe genre
                }
            }

            "前後の空白は除去される" - {
                checkAll(
                    whitespaceArb,
                    genreArb(1..15),
                    whitespaceArb,
                ) { prefix, genre, suffix ->
                    val result = Genre.create("$prefix$genre$suffix")

                    result.isOk shouldBe true
                    result.value.value shouldBe genre
                }
            }
        }

        "異常系" - {
            "空白文字のみの文字列で失敗" - {
                checkAll(whitespaceArb) { genre: String ->
                    val result = Genre.create(genre)
                    result.isErr shouldBe true
                    val error = result.error.shouldBeInstanceOf<ValidationError.Required>()
                    error.fieldName shouldBe "genre"
                }
            }

            "長すぎるジャンルで失敗" - {
                checkAll(genreArb(16..50)) { genre: String ->
                    val result = Genre.create(genre)
                    result.isErr shouldBe true
                    val error = result.error.shouldBeInstanceOf<ValidationError.InvalidFormat>()
                    error.fieldName shouldBe "genre"
                    error.rule shouldBe InvalidFormatRules.TOO_LONG
                }
            }
        }
    }
})
