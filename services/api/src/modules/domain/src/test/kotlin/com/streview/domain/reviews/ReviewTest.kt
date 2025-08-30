package com.streview.domain.reviews

import com.streview.domain.commons.UUID
import com.streview.domain.exceptions.InvalidInputException
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.bigDecimal
import io.kotest.property.arbitrary.boolean
import io.kotest.property.arbitrary.list
import io.kotest.property.arbitrary.string
import io.kotest.property.arbitrary.stringPattern
import io.kotest.property.checkAll
import java.math.BigDecimal

class ReviewTest : FreeSpec({
    val validUserIDArb = Arb.stringPattern("[a-zA-Z0-9]{28}")
    val validCommentArb = Arb.string(20..100)
    val validStarArb = Arb.bigDecimal(min = BigDecimal("0.0"), max = BigDecimal("5.0"))
    val imageUUIDsArb = Arb.list(Arb.string(36..36), range = 0..3)
    val reviewUUIDArb = Arb.string(36..36)

    "Review Domain Model" - {
        "factoryメソッドで新しいReviewを作成する" - {
            "プロパティテスト: DraftReviewありで正しく作成される" {
                checkAll(validUserIDArb, validCommentArb, validStarArb, imageUUIDsArb) {
                        writerID, comment, star, imageUUIDs ->
                    val uuids = imageUUIDs.map { UUID.generate() }
                    val draftReview = DraftReview.factory(comment, star, uuids)

                    val review = Review.create(writerID, draftReview, null)

                    review.writerID.value shouldBe writerID
                    review.draftReview shouldBe draftReview
                    review.completedReview shouldBe null
                    review.reviewUUID.value.length shouldBe 36
                }
            }

            "プロパティテスト: CompletedReviewありで正しく作成される" {
                checkAll(validUserIDArb, validCommentArb, validStarArb, imageUUIDsArb) {
                        writerID, comment, star, imageUUIDs ->
                    val uuids = imageUUIDs.map { UUID.generate() }
                    val completedReview = CompletedReview.factory(comment, star, uuids)

                    val review = Review.create(writerID, null, completedReview)

                    review.writerID.value shouldBe writerID
                    review.draftReview shouldBe null
                    review.completedReview shouldBe completedReview
                    review.reviewUUID.value.length shouldBe 36
                }
            }

            "プロパティテスト: 両方nullで正しく作成される" {
                checkAll(validUserIDArb) { writerID ->
                    val review = Review.create(writerID, null, null)

                    review.writerID.value shouldBe writerID
                    review.draftReview shouldBe null
                    review.completedReview shouldBe null
                    review.reviewUUID.value.length shouldBe 36
                }
            }
        }

        "reconstructメソッドで既存のReviewを再構築する" - {
            "プロパティテスト: 任意の値で正しく再構築される" {
                checkAll(reviewUUIDArb, validUserIDArb, validCommentArb, validStarArb, imageUUIDsArb, Arb.boolean()) {
                        reviewUUID, writerID, comment, star, imageUUIDs, hasBoth ->
                    val uuids = imageUUIDs.map { UUID.generate() }
                    val draftReview = if (hasBoth) DraftReview.factory(comment, star, uuids) else null
                    val completedReview = if (!hasBoth) CompletedReview.factory(comment, star, uuids) else null

                    val review = Review.reconstruct(reviewUUID, writerID, draftReview, completedReview)

                    review.reviewUUID.value shouldBe reviewUUID
                    review.writerID.value shouldBe writerID
                    review.draftReview shouldBe draftReview
                    review.completedReview shouldBe completedReview
                }
            }
        }

        "complete()メソッドで下書きを完了済みに変換する" - {
            "正常系" - {
                "プロパティテスト: DraftReviewがCompletedReviewに変換される" {
                    checkAll(validUserIDArb, validCommentArb, validStarArb, imageUUIDsArb) {
                            writerID, comment, star, imageUUIDs ->
                        val uuids = imageUUIDs.map { UUID.generate() }
                        val draftReview = DraftReview.factory(comment, star, uuids)
                        val review = Review.create(writerID, draftReview, null)

                        val completedReview = review.complete()

                        completedReview.writerID shouldBe review.writerID
                        completedReview.reviewUUID shouldBe review.reviewUUID
                        completedReview.draftReview shouldBe null
                        completedReview.completedReview shouldNotBe null
                        completedReview.completedReview!!.comment shouldBe draftReview.comment
                        completedReview.completedReview!!.star shouldBe draftReview.star
                        completedReview.completedReview!!.imageUUIDs shouldBe draftReview.imageUUIDs
                        completedReview.completedReview!!.isPublic shouldBe true
                    }
                }
            }

            "異常系" - {
                "プロパティテスト: DraftReviewがnullの場合、InvalidInputExceptionが発生する" {
                    checkAll(validUserIDArb) { writerID ->
                        val review = Review.create(writerID, null, null)

                        shouldThrow<InvalidInputException> {
                            review.complete()
                        }
                    }
                }
            }
        }

        "postscript()メソッドでコメントを追記する" - {
            "正常系" - {
                "プロパティテスト: CompletedReviewにコメントが追記される" {
                    checkAll(validUserIDArb, validCommentArb, validStarArb, imageUUIDsArb, validCommentArb) {
                            writerID, comment, star, imageUUIDs, additionalText ->
                        val uuids = imageUUIDs.map { UUID.generate() }
                        val completedReview = CompletedReview.factory(comment, star, uuids)
                        val review = Review.create(writerID, null, completedReview)

                        val updatedReview = review.postscript(additionalText)

                        updatedReview.writerID shouldBe review.writerID
                        updatedReview.reviewUUID shouldBe review.reviewUUID
                        updatedReview.draftReview shouldBe null
                        updatedReview.completedReview shouldNotBe null
                        updatedReview.completedReview!!.comment.value shouldBe comment + "\n" + additionalText
                    }
                }

                "プロパティテスト: CompletedReviewに星評価を更新できる" {
                    checkAll(validUserIDArb, validCommentArb, validStarArb, imageUUIDsArb, validStarArb) {
                            writerID, comment, star, imageUUIDs, newStar ->
                        val uuids = imageUUIDs.map { UUID.generate() }
                        val completedReview = CompletedReview.factory(comment, star, uuids)
                        val review = Review.create(writerID, null, completedReview)

                        val updatedReview = review.postscript(star = newStar)

                        updatedReview.completedReview!!.star.value shouldBe newStar
                        updatedReview.completedReview!!.comment.value shouldBe comment
                    }
                }

                "プロパティテスト: CompletedReviewにコメントと星評価を両方更新できる" {
                    checkAll(
                        validUserIDArb,
                        validCommentArb,
                        validStarArb,
                        imageUUIDsArb,
                        validCommentArb,
                        validStarArb
                    ) { writerID, comment, star, imageUUIDs, additionalText, newStar ->
                        val uuids = imageUUIDs.map { UUID.generate() }
                        val completedReview = CompletedReview.factory(comment, star, uuids)
                        val review = Review.create(writerID, null, completedReview)

                        val updatedReview = review.postscript(additionalText, newStar)

                        updatedReview.completedReview!!.comment.value shouldBe comment + "\n" + additionalText
                        updatedReview.completedReview.star.value shouldBe newStar
                    }
                }
            }

            "異常系" - {
                "プロパティテスト: CompletedReviewがnullの場合、InvalidInputExceptionが発生する" {
                    checkAll(validUserIDArb, validCommentArb) { writerID, additionalText ->
                        val review = Review.create(writerID, null, null)

                        shouldThrow<InvalidInputException> {
                            review.postscript(additionalText)
                        }
                    }
                }
            }
        }

        "setPrivate()メソッドでレビューを非公開にする" - {
            "プロパティテスト: CompletedReviewが非公開に設定される" {
                checkAll(validUserIDArb, validCommentArb, validStarArb, imageUUIDsArb) {
                        writerID, comment, star, imageUUIDs ->
                    val uuids = imageUUIDs.map { UUID.generate() }
                    val completedReview = CompletedReview.factory(comment, star, uuids)
                    val review = Review.create(writerID, null, completedReview)

                    val privateReview = review.setPrivate()

                    privateReview.writerID shouldBe review.writerID
                    privateReview.reviewUUID shouldBe review.reviewUUID
                    privateReview.draftReview shouldBe null
                    privateReview.completedReview shouldNotBe null
                    privateReview.completedReview!!.isPublic shouldBe false
                }
            }

            "CompletedReviewがnullの場合でも例外は発生しない" {
                checkAll(validUserIDArb) { writerID ->
                    val review = Review.create(writerID, null, null)

                    val privateReview = review.setPrivate()

                    privateReview.completedReview shouldBe null
                }
            }
        }

        "setPublic()メソッドでレビューを公開にする" - {
            "プロパティテスト: CompletedReviewが公開に設定される" {
                checkAll(validUserIDArb, validCommentArb, validStarArb, imageUUIDsArb) {
                        writerID, comment, star, imageUUIDs ->
                    val uuids = imageUUIDs.map { UUID.generate() }
                    val completedReview = CompletedReview.factory(comment, star, uuids)
                    val review = Review.create(writerID, null, completedReview)
                    val privateReview = review.setPrivate()

                    val publicReview = privateReview.setPublic()

                    publicReview.writerID shouldBe review.writerID
                    publicReview.reviewUUID shouldBe review.reviewUUID
                    publicReview.draftReview shouldBe null
                    publicReview.completedReview shouldNotBe null
                    publicReview.completedReview!!.isPublic shouldBe true
                }
            }

            "CompletedReviewがnullの場合でも例外は発生しない" {
                checkAll(validUserIDArb) { writerID ->
                    val review = Review.create(writerID, null, null)

                    val publicReview = review.setPublic()

                    publicReview.completedReview shouldBe null
                }
            }
        }
    }

    "Comment値オブジェクト" - {
        "正常系" - {
            "プロパティテスト: 20文字以上の文字列で正しく作成される" {
                val validCommentArb = Arb.string(20..100)
                checkAll(validCommentArb) { commentValue ->
                    val comment = Comment(commentValue)
                    comment.value shouldBe commentValue
                }
            }

            "境界値テスト: 20文字で正しく作成される" {
                val comment = Comment("a".repeat(20))
                comment.value shouldBe "a".repeat(20)
            }
        }

        "異常系" - {
            "空文字列でIllegalArgumentExceptionが発生する" {
                shouldThrow<IllegalArgumentException> {
                    Comment("")
                }
            }

            "19文字でIllegalArgumentExceptionが発生する" {
                shouldThrow<IllegalArgumentException> {
                    Comment("a".repeat(19))
                }
            }

            "プロパティテスト: 19文字以下でIllegalArgumentExceptionが発生する" {
                val shortCommentArb = Arb.string(0..19)
                checkAll(shortCommentArb) { shortComment ->
                    shouldThrow<IllegalArgumentException> {
                        Comment(shortComment)
                    }
                }
            }
        }
    }

    "Star値オブジェクト" - {
        "正常系" - {
            "プロパティテスト: 0.0から5.0の範囲で正しく作成される" {
                checkAll(validStarArb) { starValue ->
                    val star = Star(starValue)
                    star.value shouldBe starValue
                }
            }

            "境界値テスト: 0.0で正しく作成される" {
                val star = Star(BigDecimal("0.0"))
                star.value shouldBe BigDecimal("0.0")
            }

            "境界値テスト: 5.0で正しく作成される" {
                val star = Star(BigDecimal("5.0"))
                star.value shouldBe BigDecimal("5.0")
            }
        }

        "異常系" - {
            "負の値でIllegalArgumentExceptionが発生する" {
                shouldThrow<IllegalArgumentException> {
                    Star(BigDecimal("-0.1"))
                }
            }

            "5.0を超える値でIllegalArgumentExceptionが発生する" {
                shouldThrow<IllegalArgumentException> {
                    Star(BigDecimal("5.1"))
                }
            }

            "プロパティテスト: 範囲外の値でIllegalArgumentExceptionが発生する" {
                val invalidStarArb = Arb.bigDecimal(min = BigDecimal("-10.0"), max = BigDecimal("-0.1"))
                checkAll(invalidStarArb) { invalidStar ->
                    shouldThrow<IllegalArgumentException> {
                        Star(invalidStar)
                    }
                }
            }
        }
    }

    "具体的なテストケース" - {
        "完全なレビューライフサイクルのテスト" {
            val writerID = "abcdefghijklmnopqrstuvwxyz12"
            val comment = "これは20文字以上のコメントです。とても良い商品でした。"
            val star = BigDecimal("4.5")
            val imageUUIDs = listOf(UUID.generate(), UUID.generate())

            // 下書きレビューの作成
            val draftReview = DraftReview.factory(comment, star, imageUUIDs)
            val review = Review.create(writerID, draftReview, null)

            // 下書きを完了状態に変換
            val completedReview = review.complete()
            completedReview.completedReview!!.isPublic shouldBe true

            // コメントを追記
            val additionalText = "追加のコメントです。"
            val updatedReview = completedReview.postscript(additionalText)
            updatedReview.completedReview!!.comment.value shouldBe comment + "\n" + additionalText

            // 非公開に設定
            val privateReview = updatedReview.setPrivate()
            privateReview.completedReview!!.isPublic shouldBe false

            // 再び公開に設定
            val publicReview = privateReview.setPublic()
            publicReview.completedReview!!.isPublic shouldBe true
        }
    }
})
