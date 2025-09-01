package com.streview.application.usecases.stores

import com.github.michaelbull.result.andThen
import com.streview.application.services.ImageStorageService
import com.streview.application.services.ImageType
import com.streview.application.usecases.UseCase
import com.streview.application.usecases.stores.dto.GetNewStoreRequest
import com.streview.application.usecases.stores.dto.GetNewStoreResponse
import com.streview.application.usecases.stores.dto.ReviewRes
import com.streview.application.usecases.stores.dto.StoreRes
import com.streview.domain.commons.UserID
import com.streview.domain.relays.RelayRepository
import com.streview.domain.reviews.ReviewRepository
import com.streview.domain.stores.StoreRepository
import com.streview.domain.visits.VisitRepository
import org.jetbrains.exposed.v1.r2dbc.transactions.suspendTransaction

class GetNewStoreUseCase(
    private val imageStorageService: ImageStorageService,
    private val visitRepository: VisitRepository,
    private val storeRepository: StoreRepository,
    private val relayRepository: RelayRepository,
    private val reviewRepository: ReviewRepository,
) : UseCase<GetNewStoreRequest, GetNewStoreResponse> {
    override suspend fun execute(input: GetNewStoreRequest): GetNewStoreResponse =
        suspendTransaction {
            val userID = UserID(input.userID)

            // 訪問データからストアを取得
            val stores = visitRepository.findByUserIDAndNeutral(userID)
                .andThen { visits ->
                    storeRepository.findInUUIDs(visits.map { it.storeUUID })
                }.value

            // ユーザーのレビューを取得し既読に変更
            val reviews = relayRepository.findByUserIDAndIsNotRead(userID)
                .andThen { relays ->
                    relays.map { relay ->
                        relay.setRead()
                    }
                    relayRepository.saveAll(relays)
                }.andThen { relays ->
                    reviewRepository.findInUUIDs(relays.map { it.reviewUUID })
                }.value

            // レビューをstoreUUID別にグループ化
            val reviewsByStoreUUID = reviews.groupBy { it.storeUUID }

            // ストアとレビューをマッピング
            val storeResList = stores.map { store ->
                val storeReviews = reviewsByStoreUUID[store.storeUUID] ?: emptyList()

                val reviewResList = storeReviews.map { review ->
                    val imageUrls = review.completedReview.imageUUIDs.map { imageUUID ->
                        imageStorageService.generateUrl(imageUUID, ImageType.Review)
                    }
                    ReviewRes(
                        reviewUUID = review.reviewUUID.value,
                        comment = review.completedReview.comment.value,
                        star = review.completedReview.star.value.toDouble(),
                        createdAt = review.completedReview.createdAt,
                        imageUrls = imageUrls,
                    )
                }

                StoreRes(
                    storeUUID = store.storeUUID.value,
                    name = store.name.value,
                    genre = store.genre.value,
                    address = store.address.value,
                    tel = store.tel.value,
                    description = store.description.value,
                    open = store.open.value,
                    reviews = reviewResList
                )
            }
            GetNewStoreResponse(
                stores = storeResList
            )
        }
}
