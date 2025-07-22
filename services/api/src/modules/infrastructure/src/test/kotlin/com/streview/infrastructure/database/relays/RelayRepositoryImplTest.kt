package com.streview.infrastructure.database.relays

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

                        repository.save(relay)

                        val savedRelay = repository.findByUserIdAndReviewUUID(userID, reviewUUID)
                        savedRelay shouldNotBe null
                        savedRelay!!.userID.value shouldBe userID
                        savedRelay.reviewUUID.value shouldBe reviewUUID
                        savedRelay.isReReviewed shouldBe false

                        rollback()
                    }
                }
            }

            "プロパティテスト: isReReviewedがtrueのrelayを保存できること" {
                checkAll(validUserIDArb, reviewUUIDArb) { userID, reviewUUID ->
                    suspendTransaction {
                        val relay = Relay.reconstruct(userID, reviewUUID, true)

                        repository.save(relay)

                        val savedRelay = repository.findByUserIdAndReviewUUID(userID, reviewUUID)
                        savedRelay shouldNotBe null
                        savedRelay!!.userID.value shouldBe userID
                        savedRelay.reviewUUID.value shouldBe reviewUUID
                        savedRelay.isReReviewed shouldBe true

                        rollback()
                    }
                }
            }

            "プロパティテスト: 同じキーのrelayを更新できること（upsert）" {
                checkAll(validUserIDArb, reviewUUIDArb) { userID, reviewUUID ->
                    suspendTransaction {
                        val originalRelay = Relay.factory(userID, reviewUUID)
                        repository.save(originalRelay)

                        val updatedRelay = Relay.reconstruct(userID, reviewUUID, true)
                        repository.save(updatedRelay)

                        val savedRelay = repository.findByUserIdAndReviewUUID(userID, reviewUUID)
                        savedRelay shouldNotBe null
                        savedRelay!!.userID.value shouldBe userID
                        savedRelay.reviewUUID.value shouldBe reviewUUID
                        savedRelay.isReReviewed shouldBe true

                        rollback()
                    }
                }
            }
        }

        "findByUserIdAndReviewUUIDメソッドのプロパティテスト" - {
            "プロパティテスト: 存在するrelayを正しく取得できること" {
                checkAll(validUserIDArb, reviewUUIDArb, Arb.boolean()) { userID, reviewUUID, isReReviewed ->
                    suspendTransaction {
                        val originalRelay = Relay.reconstruct(userID, reviewUUID, isReReviewed)
                        repository.save(originalRelay)

                        val foundRelay = repository.findByUserIdAndReviewUUID(userID, reviewUUID)

                        foundRelay shouldNotBe null
                        foundRelay!!.userID.value shouldBe userID
                        foundRelay.reviewUUID.value shouldBe reviewUUID
                        foundRelay.isReReviewed shouldBe isReReviewed

                        rollback()
                    }
                }
            }

            "プロパティテスト: 存在しないrelayの場合nullを返すこと" {
                checkAll(validUserIDArb, reviewUUIDArb) { userID, reviewUUID ->
                    suspendTransaction {
                        val foundRelay = repository.findByUserIdAndReviewUUID(userID, reviewUUID)
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
                            repository.save(relay)

                            val foundRelay = repository.findByUserIdAndReviewUUID(userID, wrongReviewUUID)
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
                            repository.save(relay)

                            val foundRelay = repository.findByUserIdAndReviewUUID(wrongUserID, reviewUUID)
                            foundRelay shouldBe null

                            rollback()
                        }
                    }
                }
            }
        }

        "save-findサイクルのプロパティテスト" - {
            "プロパティテスト: 保存と取得の整合性が保たれること" {
                checkAll(validUserIDArb, reviewUUIDArb, Arb.boolean()) { userID, reviewUUID, isReReviewed ->
                    suspendTransaction {
                        val originalRelay = Relay.reconstruct(userID, reviewUUID, isReReviewed)

                        repository.save(originalRelay)
                        val retrievedRelay = repository.findByUserIdAndReviewUUID(userID, reviewUUID)

                        retrievedRelay shouldNotBe null
                        retrievedRelay!!.userID.value shouldBe originalRelay.userID.value
                        retrievedRelay.reviewUUID.value shouldBe originalRelay.reviewUUID.value
                        retrievedRelay.isReReviewed shouldBe originalRelay.isReReviewed

                        rollback()
                    }
                }
            }

            "プロパティテスト: 複数のrelayを保存しても正しく区別して取得できること" {
                checkAll(validUserIDArb, validUserIDArb, reviewUUIDArb, reviewUUIDArb) { userID1, userID2, reviewUUID1, reviewUUID2 ->
                    if (userID1 != userID2 || reviewUUID1 != reviewUUID2) {
                        suspendTransaction {
                            val relay1 = Relay.reconstruct(userID1, reviewUUID1, false)
                            val relay2 = Relay.reconstruct(userID2, reviewUUID2, true)

                            repository.save(relay1)
                            repository.save(relay2)

                            val foundRelay1 = repository.findByUserIdAndReviewUUID(userID1, reviewUUID1)
                            val foundRelay2 = repository.findByUserIdAndReviewUUID(userID2, reviewUUID2)

                            foundRelay1 shouldNotBe null
                            foundRelay2 shouldNotBe null
                            foundRelay1!!.userID.value shouldBe userID1
                            foundRelay1.reviewUUID.value shouldBe reviewUUID1
                            foundRelay1.isReReviewed shouldBe false
                            foundRelay2!!.userID.value shouldBe userID2
                            foundRelay2.reviewUUID.value shouldBe reviewUUID2
                            foundRelay2.isReReviewed shouldBe true

                            rollback()
                        }
                    }
                }
            }
        }
    }
})
