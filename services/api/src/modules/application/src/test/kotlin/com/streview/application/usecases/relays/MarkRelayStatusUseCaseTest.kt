package com.streview.application.usecases.relays

import io.kotest.core.spec.style.FreeSpec

class MarkRelayStatusUseCaseTest : FreeSpec({
//
//    val validUserIDArb = Arb.stringPattern("[a-zA-Z0-9]{28}")
//    val reviewUUIDArb = Arb.uuid(UUIDVersion.V4).map { it.toString() }
//
//    "MarkRelayStatusUseCaseの正常系テスト" - {
//        "プロパティテスト: toggleStatus=trueの場合にreReview処理が正しく実行される" {
//            checkAll(validUserIDArb, reviewUUIDArb) { userID, reviewUUID ->
//                // 各テスト実行前にモックをクリア
//                clearAllMocks()
//
//                val mockRepository = mockk<RelayRepository>()
//                val mockDomainService = mockk<RelayDomainService>()
//                val mockRelay = mockk<Relay>(relaxed = true)
//                val mockUpdatedRelay = mockk<Relay>(relaxed = true)
//
//                // モックの設定
//                coEvery { mockRepository.findByUserIdAndReviewUUID(userID, reviewUUID) } returns Ok(mockRelay)
//                coEvery { mockDomainService.reReview(mockRelay) } returns mockUpdatedRelay
//                coEvery { mockRepository.save(mockUpdatedRelay) } returns Ok(mockUpdatedRelay)
//                every { mockUpdatedRelay.reviewUUID.value } returns reviewUUID
//
//                // テスト実行
//                val useCase = MarkRelayStatusUseCase(mockRepository, mockDomainService)
//                val request = RelayStatusToggleRequest(userID, reviewUUID, true)
//
//                val response = useCase.execute(request)
//
//                // 検証
//                response.reviewUUID shouldBe reviewUUID
//                coVerify(exactly = 1) { mockRepository.findByUserIdAndReviewUUID(userID, reviewUUID) }
//                coVerify(exactly = 1) { mockDomainService.reReview(mockRelay) }
//                coVerify(exactly = 1) { mockRepository.save(mockUpdatedRelay) }
//            }
//        }
//
//        "プロパティテスト: toggleStatus=falseの場合にunsetReReview処理が正しく実行される" {
//            checkAll(validUserIDArb, reviewUUIDArb) { userID, reviewUUID ->
//                // 各テスト実行前にモックをクリア
//                clearAllMocks()
//
//                val mockRepository = mockk<RelayRepository>()
//                val mockDomainService = mockk<RelayDomainService>()
//                val mockRelay = mockk<Relay>(relaxed = true)
//
//                // モックの設定
//                coEvery { mockRepository.findByUserIdAndReviewUUID(userID, reviewUUID) } returns Ok(mockRelay)
//                coEvery { mockRepository.save(mockRelay) } returns Ok(mockRelay)
//                every { mockRelay.reviewUUID.value } returns reviewUUID
//                every { mockRelay.unsetReReview() } returns Unit
//
//                // テスト実行
//                val useCase = MarkRelayStatusUseCase(mockRepository, mockDomainService)
//                val request = RelayStatusToggleRequest(userID, reviewUUID, false)
//
//                val response = useCase.execute(request)
//
//                // 検証
//                response.reviewUUID shouldBe reviewUUID
//                coVerify(exactly = 1) { mockRepository.findByUserIdAndReviewUUID(userID, reviewUUID) }
//                coVerify(exactly = 0) { mockDomainService.reReview(any()) } // falseの場合は呼ばれない
//                coVerify(exactly = 1) { mockRepository.save(mockRelay) }
//                coVerify(exactly = 1) { mockRelay.unsetReReview() }
//            }
//        }
//    }
//
//    "MarkRelayStatusUseCaseの異常系テスト" - {
//        "プロパティテスト: 対象のRelayが存在しない場合にInvalidInputExceptionが発生する" {
//            checkAll(validUserIDArb, reviewUUIDArb, Arb.boolean()) { userID, reviewUUID, toggleStatus ->
//                // 各テスト実行前にモックをクリア
//                clearAllMocks()
//
//                // モックの設定（nullを返す）
//                val mockRepository = mockk<RelayRepository>()
//                val mockDomainService = mockk<RelayDomainService>()
//                coEvery { mockRepository.findByUserIdAndReviewUUID(userID, reviewUUID) } returns Ok(null)
//
//                // テスト実行
//                val useCase = MarkRelayStatusUseCase(mockRepository, mockDomainService)
//                val request = RelayStatusToggleRequest(userID, reviewUUID, toggleStatus)
//
//                val exception = shouldThrow<InvalidInputException> {
//                    useCase.execute(request)
//                }
//
//                // 検証
//                exception.message shouldBe "対象のレビューが存在しません。"
//                coVerify(exactly = 1) { mockRepository.findByUserIdAndReviewUUID(userID, reviewUUID) }
//                coVerify(exactly = 0) { mockDomainService.reReview(any()) }
//                coVerify(exactly = 0) { mockRepository.save(any()) }
//            }
//        }
//    }
})
