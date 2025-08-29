package com.streview.domain.commons

import com.streview.domain.commons.errors.ValidationError
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import io.kotest.property.Arb
import io.kotest.property.arbitrary.*
import io.kotest.property.checkAll

class GeoLocationTest : FreeSpec({
    "GeoLocationのvalue object テスト" - {
        val latitudeArb = Arb.double(min = -90.0, max = 90.0)
        val longitudeArb = Arb.double(min = -180.0, max = 180.0)

        "正常系" - {
            "規定範囲内の数値で生成" - {
                checkAll(latitudeArb, longitudeArb) { lat, lon ->
                    val result = GeoLocation.create(lat, lon)

                    result.isOk shouldBe true
                    result.value.latitude shouldBe lat
                    result.value.longitude shouldBe lon
                }
            }
        }
        "異常系" - {
            val invalidLatitudeArb = Arb.choice(
                Arb.double(min = -180.0, max = -90.1),
                Arb.double(min = 90.1, max = 180.0)
            )
            val invalidLongitudeArb = Arb.choice(
                Arb.double(min = 180.1, max = 360.0),
                Arb.double(min = -360.0, max = -180.1)
            )
            "latitudeが規定範囲外でエラー" - {
                checkAll(invalidLatitudeArb, longitudeArb) { lat, lon ->
                    val result = GeoLocation.create(lat, lon)

                    result.isErr shouldBe true
                    val error = result.error.shouldBeInstanceOf<ValidationError.InvalidFormat>()
                    error.fieldName shouldBe "latitude"
                }
            }
            "longitudeが規定範囲外でエラー" - {
                checkAll(latitudeArb, invalidLongitudeArb) { lat, lon ->
                    val result = GeoLocation.create(lat, lon)

                    result.isErr shouldBe true
                    val error = result.error.shouldBeInstanceOf<ValidationError.InvalidFormat>()
                    error.fieldName shouldBe "longitude"
                }
            }
            "両方の値が規定範囲外で複数例外" - {
                checkAll(invalidLatitudeArb, invalidLongitudeArb) { lat, lon ->
                    val result = GeoLocation.create(lat, lon)

                    result.isErr shouldBe true
                    val error = result.error.shouldBeInstanceOf<ValidationError.Multiple>()
                    error.errors.size shouldBe 2
                    error.errors.first()
                        .shouldBeInstanceOf<ValidationError.InvalidFormat>()
                        .fieldName shouldBe "latitude"
                    error.errors.last()
                        .shouldBeInstanceOf<ValidationError.InvalidFormat>()
                        .fieldName shouldBe "longitude"
                }
            }
        }
    }
})
