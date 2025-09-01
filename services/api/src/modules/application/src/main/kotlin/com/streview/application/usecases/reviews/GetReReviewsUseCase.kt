package com.streview.application.usecases.reviews

import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.andThen
import com.github.michaelbull.result.fold
import com.streview.application.services.ImageStorageService
import com.streview.application.services.ImageType
import com.streview.application.usecases.UseCase
import com.streview.application.usecases.reviews.dto.GetReReviewRequest
import com.streview.application.usecases.reviews.dto.GetReReviewResponse
import com.streview.application.usecases.reviews.dto.Review
import com.streview.domain.commons.UserID
import com.streview.domain.relays.RelayRepository
import com.streview.domain.reviews.ReviewRepository
import com.streview.domain.stores.StoreRepository
import org.jetbrains.exposed.v1.r2dbc.transactions.suspendTransaction

class GetReReviewsUseCase(
    private val relayRepository: RelayRepository,
    private val reviewRepository: ReviewRepository,
    private val storeRepository: StoreRepository,
    private val imageStorageService: ImageStorageService,
) : UseCase<GetReReviewRequest, GetReReviewResponse> {
    override suspend fun execute(input: GetReReviewRequest): GetReReviewResponse =
        suspendTransaction {
            val userID = UserID(input.userID)

            relayRepository.findReReviewByUserId(userID)
                .andThen { reReviews ->
                    reviewRepository.findInUUIDs(reReviews.map { it.reviewUUID })
                }
                .andThen { reviews ->
                    val storeUUIDs = reviews.map { it.storeUUID }.toSet().toList()
                    storeRepository.findInUUIDs(storeUUIDs)
                        .andThen { stores ->
                            val storeMap = stores.associateBy { it.storeUUID }

                            val reviewList = reviews.map { review ->
                                val storeName = storeMap[review.storeUUID]?.let {
                                    it.name.value
                                } ?: "取得エラー"

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
                                    storeName = storeName,
                                    storeUUID = review.storeUUID.value
                                )
                            }
                            Ok(reviewList)
                        }
                }
        }.fold(
            success = {
                GetReReviewResponse(it)
            },
            failure = {
                throw it
            }
        )
}
