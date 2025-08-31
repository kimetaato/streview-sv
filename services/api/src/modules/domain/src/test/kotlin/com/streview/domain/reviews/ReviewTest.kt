package com.streview.domain.reviews

import com.streview.domain.commons.UUID
import com.streview.domain.commons.UserID
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.Codepoint
import io.kotest.property.arbitrary.ascii
import io.kotest.property.arbitrary.bigDecimal
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.string
import io.kotest.property.arbitrary.stringPattern
import io.kotest.property.checkAll
import java.math.BigDecimal

class ReviewTest : FreeSpec({
    val validUserIDArb = Arb.stringPattern("[a-zA-Z0-9]{28}")
    val validCommentArb = Arb.string(20..100)
    val validStarArb = Arb.bigDecimal(min = BigDecimal("0.0"), max = BigDecimal("5.0"))
    val imageCountArb = Arb.int(range = 1..5)
    val uuidArb = Arb.string(36..36, codepoints = Codepoint.ascii())
    val reviewUUIDArb = Arb.string(36..36, codepoints = Codepoint.ascii())

    "Review Domain Model" - {
        "factoryメソッドで新しいReviewを作成する" - {
            "プロパティテスト: CompletedReviewありで正しく作成される" {
                checkAll(validUserIDArb, uuidArb, validCommentArb, validStarArb, imageCountArb) {
                        writerID, storeUUID, comment, star, imageCount ->
                    val uuids = List(imageCount) { UUID.generate() }
                    val completedReview = CompletedReview.create(comment, star, uuids)
                    val writer = UserID(writerID)

                    val review = Review.create(writer, UUID.generate(storeUUID), completedReview)

                    review.writerID.value shouldBe writerID
                    review.completedReview shouldBe completedReview
                    review.reviewUUID.value.length shouldBe 36
                }
            }
        }

        "reconstructメソッドで既存のReviewを再構築する" - {
            "プロパティテスト: CompletedReviewで正しく再構築される" {
                checkAll(reviewUUIDArb, validUserIDArb, uuidArb, validCommentArb, validStarArb, imageCountArb) {
                        reviewUUID, writerID, storeUUID, comment, star, imageCount ->
                    val uuids = List(imageCount) { UUID.generate() }
                    val completedReview = CompletedReview.create(comment, star, uuids)

                    val review = Review.reconstruct(reviewUUID, writerID, storeUUID, completedReview)

                    review.reviewUUID.value shouldBe reviewUUID
                    review.writerID.value shouldBe writerID
                    review.storeUUID.value shouldBe storeUUID
                    review.completedReview shouldBe completedReview
                }
            }
        }

        "postscript()メソッドでコメントを追記する" - {
            "正常系" - {
                "プロパティテスト: CompletedReviewにコメントが追記される" {
                    checkAll(validUserIDArb, uuidArb, validCommentArb, validStarArb, imageCountArb, validCommentArb) {
                            writerID, storeUUID, comment, star, imageCount, additionalText ->
                        val uuids = List(imageCount) { UUID.generate() }
                        val completedReview = CompletedReview.create(comment, star, uuids)
                        val writer = UserID(writerID)

                        val review = Review.create(writer, UUID.generate(storeUUID), completedReview)

                        val updatedReview = review.postscript(additionalText)

                        updatedReview.writerID shouldBe review.writerID
                        updatedReview.reviewUUID shouldBe review.reviewUUID
                        updatedReview.completedReview.comment.value shouldBe comment + "\n" + additionalText
                    }
                }

                "プロパティテスト: CompletedReviewに星評価を更新できる" {
                    checkAll(validUserIDArb, uuidArb, validCommentArb, validStarArb, imageCountArb, validStarArb) {
                            writerID, storeUUID, comment, star, imageCount, newStar ->
                        val uuids = List(imageCount) { UUID.generate() }
                        val completedReview = CompletedReview.create(comment, star, uuids)
                        val writer = UserID(writerID)

                        val review = Review.create(writer, UUID.generate(storeUUID), completedReview)

                        val updatedReview = review.postscript(star = newStar)

                        updatedReview.completedReview.star.value shouldBe newStar
                    }
                }

                "プロパティテスト: CompletedReviewにコメントと星評価を両方更新できる" {
                    checkAll(
                        validUserIDArb,
                        uuidArb,
                        validCommentArb,
                        validStarArb,
                        imageCountArb,
                        validCommentArb,
                        validStarArb
                    ) { writerID, storeUUID, comment, star, imageCount, additionalText, newStar ->
                        val uuids = List(imageCount) { UUID.generate() }
                        val completedReview = CompletedReview.create(comment, star, uuids)
                        val writer = UserID(writerID)

                        val review = Review.create(writer, UUID.generate(storeUUID), completedReview)

                        val updatedReview = review.postscript(additionalText, newStar)

                        updatedReview.completedReview.comment.value shouldBe comment + "\n" + additionalText
                        updatedReview.completedReview.star.value shouldBe newStar
                    }
                }
            }
        }

        "setPrivate()メソッドでレビューを非公開にする" - {
            "プロパティテスト: CompletedReviewが非公開に設定される" {
                checkAll(validUserIDArb, uuidArb, validCommentArb, validStarArb, imageCountArb) {
                        writerID, storeUUID, comment, star, imageCount ->
                    val uuids = List(imageCount) { UUID.generate() }
                    val completedReview = CompletedReview.create(comment, star, uuids)
                    val writer = UserID(writerID)

                    val review = Review.create(writer, UUID.generate(storeUUID), completedReview)

                    val privateReview = review.setPrivate()

                    privateReview.writerID shouldBe review.writerID
                    privateReview.reviewUUID shouldBe review.reviewUUID
                    privateReview.completedReview.isPublic shouldBe false
                }
            }
        }

        "setPublic()メソッドでレビューを公開にする" - {
            "プロパティテスト: CompletedReviewが公開に設定される" {
                checkAll(validUserIDArb, uuidArb, validCommentArb, validStarArb, imageCountArb) {
                        writerID, storeUUID, comment, star, imageCount ->
                    val uuids = List(imageCount) { UUID.generate() }
                    val completedReview = CompletedReview.create(comment, star, uuids)
                    val writer = UserID(writerID)

                    val review = Review.create(writer, UUID.generate(storeUUID), completedReview)
                    val privateReview = review.setPrivate()

                    val publicReview = privateReview.setPublic()

                    publicReview.completedReview.isPublic shouldBe true
                }
            }
        }
    }
})
