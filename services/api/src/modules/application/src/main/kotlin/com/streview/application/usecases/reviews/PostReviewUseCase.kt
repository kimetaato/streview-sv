package com.streview.application.usecases.reviews

import com.github.michaelbull.result.fold
import com.github.michaelbull.result.getOrThrow
import com.github.michaelbull.result.map
import com.streview.application.services.ImageStorageService
import com.streview.application.services.ImageType
import com.streview.application.usecases.UseCase
import com.streview.application.usecases.reviews.dto.PostReviewRequest
import com.streview.application.usecases.reviews.dto.PostReviewResponse
import com.streview.domain.commons.UUID
import com.streview.domain.commons.UserID
import com.streview.domain.images.Image
import com.streview.domain.images.ImageRepository
import com.streview.domain.reviews.CompletedReview
import com.streview.domain.reviews.Review
import com.streview.domain.reviews.ReviewRepository
import kotlinx.datetime.Clock
import org.jetbrains.exposed.v1.r2dbc.transactions.suspendTransaction
import kotlin.random.Random

class PostReviewUseCase(
    private val imageStorageService: ImageStorageService,
    private val imageRepository: ImageRepository,
    private val reviewRepository: ReviewRepository
) : UseCase<PostReviewRequest, PostReviewResponse> {
    override suspend fun execute(input: PostReviewRequest): PostReviewResponse =
        suspendTransaction {
            val userID = UserID(input.userID)
            val storeUUID = UUID.generate(input.storeUUID)

            val imageUUIDs = input.imageSources.map { imageSource ->
                val fileName = "${Clock.System.now().toEpochMilliseconds()}_${Random.nextInt(1000, 10000)}"
                // 画像を保存する
                imageStorageService.save(imageSource, fileName, ImageType.Review)

                val image: Image = Image.create(fileName)

                // 画像パスを登録する
                imageRepository.save(image).map { savedImage -> savedImage.imageUUID }.getOrThrow()
            }

            val completedReview = CompletedReview.create(
                comment = input.comment,
                star = input.star,
                imageUUIDs = imageUUIDs
            )

            val review = Review.create(userID, storeUUID, completedReview)

            reviewRepository.save(review).fold(
                success = { savedReview ->
                    PostReviewResponse(
                        savedReview.reviewUUID.value
                    )
                },
                failure = {
                    throw it
                }
            )
        }
}
