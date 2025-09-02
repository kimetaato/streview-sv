package com.streview.application.usecases.reviews

import com.github.michaelbull.result.andThen
import com.github.michaelbull.result.fold
import com.github.michaelbull.result.map
import com.streview.application.services.ImageStorageService
import com.streview.application.services.ImageType
import com.streview.application.usecases.UseCase
import com.streview.common.dto.reviews.GetMyReviewsRequest
import com.streview.common.dto.reviews.GetMyReviewsResponse
import com.streview.common.dto.reviews.Review
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

            reviewRepository.findByWriterID(userID)
                .andThen { reviews ->
                    storeRepository.findInStoreUUIDs(reviews.map { it.storeUUID })
                        .map { stores ->
                            reviews.map { review ->
                                Pair(review, stores.find { store -> store.storeUUID == review.storeUUID }!!)
                            }
                        }
                }
        }.fold(
            success = { reviews ->
                GetMyReviewsResponse(
                    reviews = reviews.map { (review, store) ->
                        val imageUrls = review.completedReview.imageUUIDs.map {
                            imageStorageService.generateUrl(it, ImageType.Review)
                        }
                        Review(
                            reviewUUID = review.reviewUUID.value,
                            comment = review.completedReview.comment.value,
                            star = review.completedReview.star.value.toDouble(),
                            imageUrls = imageUrls,
                            createdAt = review.completedReview.createdAt,
                            updatedAt = review.completedReview.updatedAt,
                            storeName = store.name.value,
                            storeUUID = review.storeUUID.value,
                        )
                    }
                )
            },
            failure = {
                throw it
            }
        )
}
