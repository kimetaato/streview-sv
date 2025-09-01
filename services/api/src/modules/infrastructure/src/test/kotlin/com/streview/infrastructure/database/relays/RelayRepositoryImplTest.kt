package com.streview.infrastructure.database.relays

import com.github.michaelbull.result.getOrThrow
import com.streview.domain.relays.Relay
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.UUIDVersion
import io.kotest.property.arbitrary.boolean
import io.kotest.property.arbitrary.map
import io.kotest.property.arbitrary.stringPattern
import io.kotest.property.arbitrary.uuid
import io.kotest.property.checkAll
import org.jetbrains.exposed.v1.r2dbc.transactions.suspendTransaction

class RelayRepositoryImplTest : FreeSpec({

    val repository = RelayRepositoryImpl()

    val validUserIDArb = Arb.stringPattern("[a-zA-Z0-9]{28}")
    val reviewUUIDArb = Arb.uuid(UUIDVersion.V4).map { it.toString() }

    "RelayRepositoryImplの統合テスト" - {
        "saveメソッドのプロパティテスト" - {
            "プロパティテスト: 任意のrelayを保存できること" {
                checkAll(validUserIDArb, reviewUUIDArb) { userID, reviewUUID ->
                    suspendTransaction {
                        val relay = Relay.factory(userID, reviewUUID)

                        repository.save(relay).getOrThrow()

                        val savedRelay = repository.findByUserIdAndReviewUUID(userID, reviewUUID).getOrThrow()
                        savedRelay shouldNotBe null
                        savedRelay!!.userID.value shouldBe userID
                        savedRelay.reviewUUID.value shouldBe reviewUUID
                        savedRelay.isReReview shouldBe false

                        rollback()
                    }
                }
            }
            "プロパティテスト: 同じキーのrelayを更新できること（upsert）" {
                checkAll(validUserIDArb, reviewUUIDArb) { userID, reviewUUID ->
                    suspendTransaction {
                        val originalRelay = Relay.factory(userID, reviewUUID)
                        repository.save(originalRelay).getOrThrow()

                        val updatedRelay = Relay.reconstruct(
                            userID = userID,
                            reviewUUID = reviewUUID,
                            isReReview = true,
                            isRead = true
                        )
                        repository.save(updatedRelay).getOrThrow()

                        val savedRelay = repository.findByUserIdAndReviewUUID(userID, reviewUUID).getOrThrow()
                        savedRelay shouldNotBe null
                        savedRelay!!.userID.value shouldBe userID
                        savedRelay.reviewUUID.value shouldBe reviewUUID
                        savedRelay.isReReview shouldBe true

                        rollback()
                    }
                }
            }
        }

        "findByUserIdAndReviewUUIDメソッドのプロパティテスト" - {
            "プロパティテスト: 存在するrelayを正しく取得できること" {
                checkAll(
                    validUserIDArb,
                    reviewUUIDArb,
                    Arb.boolean(),
                    Arb.boolean()
                ) { userID, reviewUUID, isReReview, isRead ->
                    suspendTransaction {
                        val originalRelay = Relay.reconstruct(
                            userID = userID,
                            reviewUUID = reviewUUID,
                            isReReview = isReReview,
                            isRead = isRead
                        )
                        repository.save(originalRelay).getOrThrow()

                        val foundRelay = repository.findByUserIdAndReviewUUID(userID, reviewUUID).getOrThrow()

                        foundRelay shouldNotBe null
                        foundRelay!!.userID.value shouldBe userID
                        foundRelay.reviewUUID.value shouldBe reviewUUID
                        foundRelay.isReReview shouldBe isReReview

                        rollback()
                    }
                }
            }

            "プロパティテスト: 存在しないrelayの場合nullを返すこと" {
                checkAll(validUserIDArb, reviewUUIDArb) { userID, reviewUUID ->
                    suspendTransaction {
                        val foundRelay = repository.findByUserIdAndReviewUUID(userID, reviewUUID).getOrThrow()
                        foundRelay shouldBe null

                        rollback()
                    }
                }
            }

            "プロパティテスト: userIDが一致してもreviewUUIDが異なる場合nullを返すこと" {
                checkAll(validUserIDArb, reviewUUIDArb, reviewUUIDArb) { userID, correctReviewUUID, wrongReviewUUID ->
                    if (correctReviewUUID != wrongReviewUUID) {
                        suspendTransaction {
                            val relay = Relay.factory(userID, correctReviewUUID)
                            repository.save(relay).getOrThrow()

                            val foundRelay = repository.findByUserIdAndReviewUUID(userID, wrongReviewUUID).getOrThrow()
                            foundRelay shouldBe null

                            rollback()
                        }
                    }
                }
            }

            "プロパティテスト: reviewUUIDが一致してもuserIDが異なる場合nullを返すこと" {
                checkAll(validUserIDArb, validUserIDArb, reviewUUIDArb) { correctUserID, wrongUserID, reviewUUID ->
                    if (correctUserID != wrongUserID) {
                        suspendTransaction {
                            val relay = Relay.factory(correctUserID, reviewUUID)
                            repository.save(relay).getOrThrow()

                            val foundRelay = repository.findByUserIdAndReviewUUID(wrongUserID, reviewUUID).getOrThrow()
                            foundRelay shouldBe null

                            rollback()
                        }
                    }
                }
            }
        }
    }
})
