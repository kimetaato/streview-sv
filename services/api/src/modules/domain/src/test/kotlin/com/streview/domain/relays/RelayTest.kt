package com.streview.domain.relays

import com.streview.domain.exceptions.InvalidInputException
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.UUIDVersion
import io.kotest.property.arbitrary.boolean
import io.kotest.property.arbitrary.map
import io.kotest.property.arbitrary.stringPattern
import io.kotest.property.arbitrary.uuid
import io.kotest.property.checkAll

class RelayTest : FreeSpec({
    val validUserIDArb = Arb.stringPattern("[a-zA-Z0-9]{28}")
    val reviewUUIDArb = Arb.uuid(UUIDVersion.V4).map { it.toString() }

    "Relay Domain Model" - {
        "ファクトリーメソッドで新しいRelayを作成する" - {
            "プロパティテスト: 任意のuserIDとReviewUUIDで作成" {
                checkAll(validUserIDArb, reviewUUIDArb) { userID, reviewUUID ->
                    val relay = Relay.factory(userID, reviewUUID)
                    relay.userID.value shouldBe userID
                    relay.reviewUUID.value shouldBe reviewUUID
                    relay.isReReviewed shouldBe false
                }
            }
        }
        "再生成メソッドでRelayを作成する" - {
            checkAll(validUserIDArb, reviewUUIDArb, Arb.boolean()) { userID, reviewUUID, isReReview ->
                val relay = Relay.reconstruct(userID, reviewUUID, isReReview)
                relay.userID.value shouldBe userID
                relay.reviewUUID.value shouldBe reviewUUID
                relay.isReReviewed shouldBe isReReview
            }
        }
        "振る舞いメソッドでisRelayをtrueにする" - {
            checkAll(validUserIDArb, reviewUUIDArb) { userID, reviewUUID ->
                val relay = Relay.reconstruct(userID, reviewUUID, false)
                relay.setReReview()
                relay.userID.value shouldBe userID
                relay.reviewUUID.value shouldBe reviewUUID
                relay.isReReviewed shouldBe true
            }
        }
        "振る舞いメソッドでisRelayをfalseにする" - {
            checkAll(validUserIDArb, reviewUUIDArb) { userID, reviewUUID ->
                val relay = Relay.reconstruct(userID, reviewUUID, true)

                relay.unsetReReview()

                relay.userID.value shouldBe userID
                relay.reviewUUID.value shouldBe reviewUUID
                relay.isReReviewed shouldBe false
            }
        }

        "すでに公開設定のものを公開設定しようとするとエラーになる" - {
            checkAll(validUserIDArb, reviewUUIDArb) { userID, reviewUUID ->
                val relay = Relay.reconstruct(userID, reviewUUID, false)
                shouldThrow<InvalidInputException> {
                    relay.unsetReReview()
                }
            }
        }

        "すでに日共有状態のものを非共有にしようとするとエラーになる" - {
            checkAll(validUserIDArb, reviewUUIDArb) { userID, reviewUUID ->
                val relay = Relay.reconstruct(userID, reviewUUID, true)
                shouldThrow<InvalidInputException> {
                    relay.setReReview()
                }
            }
        }
    }
})
