package com.streview.application.usecases.reviews

import com.github.michaelbull.result.fold
import com.streview.application.services.ImageStorageService
import com.streview.application.services.ImageType
import com.streview.application.usecases.UseCase
import com.streview.application.usecases.reviews.dto.GetMyReviewsRequest
import com.streview.application.usecases.reviews.dto.GetMyReviewsResponse
import com.streview.application.usecases.reviews.dto.Review
import com.streview.domain.commons.UserID
import com.streview.domain.reviews.ReviewRepository
import com.streview.domain.stores.StoreRepository
import org.jetbrains.exposed.v1.r2dbc.transactions.suspendTransaction

class GetMyReviewUseCase(
    private val reviewRepository: ReviewRepository,
    private val imageStorageService: ImageStorageService,
    private val storeRepository: StoreRepository
) : UseCase<GetMyReviewsRequest, GetMyReviewsResponse> {
    override suspend fun execute(input: GetMyReviewsRequest): GetMyReviewsResponse =
        suspendTransaction {
            val userID = UserID(input.userID)

            reviewRepository.findByWriterID(userID).fold(
                success = {
                    GetMyReviewsResponse(
                        it.map { review ->
                            val storeName = storeRepository.findByUUID(review.storeUUID).fold(
                                success = { store ->
                                    store?.name?.value ?: "取得エラー"
                                },
                                failure = {
                                    throw it
                                }
                            )

                            val imageUrls = review.completedReview.imageUUIDs.map { imageUUID ->
                                imageStorageService.generateUrl(imageUUID, ImageType.Review)
                            }
                            Review(
                                reviewUUID = review.reviewUUID.value,
                                comment = review.completedReview.comment.value,
                                star = review.completedReview.star.value.toDouble(),
                                createdAt = review.completedReview.createdAt,
                                updatedAt = review.completedReview.updatedAt,
                                storeName = storeName,
                                storeUUID = review.storeUUID.value,
                                imageUrls = imageUrls
                            )
                        }
                    )
                },
                failure = {
                    throw it
                }
            )
        }
}
