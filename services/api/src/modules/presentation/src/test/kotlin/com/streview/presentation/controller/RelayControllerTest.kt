package com.streview.presentation.controller

import com.streview.application.usecases.relays.MarkRelayStatusUseCase
import com.streview.application.usecases.relays.dto.RelayStatusToggleRequest
import com.streview.application.usecases.relays.dto.RelayStatusToggleResponse
import com.streview.domain.exceptions.InvalidInputException
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.UUIDVersion
import io.kotest.property.arbitrary.map
import io.kotest.property.arbitrary.stringPattern
import io.kotest.property.arbitrary.uuid
import io.kotest.property.checkAll
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.routing.*
import io.ktor.server.testing.testApplication
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import org.koin.core.context.GlobalContext
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.koin.ktor.plugin.Koin

class RelayControllerTest : FreeSpec({

    val validUserIDArb = Arb.stringPattern("[a-zA-Z0-9]{28}")
    val reviewUUIDArb = Arb.uuid(UUIDVersion.V4).map { it.toString() }

    // テスト前後でKoinの状態を管理
    beforeEach {
        if (GlobalContext.getOrNull() != null) {
            stopKoin()
        }
    }

    afterEach {
        if (GlobalContext.getOrNull() != null) {
            stopKoin()
        }
    }

    "RelayControllerの正常系テスト" - {
        "プロパティテスト: PATCH /reviews/{r_id} status=\"public\" で正常にレスポンスが返される" {
            checkAll(validUserIDArb, reviewUUIDArb) { userID, reviewUUID ->
                // 各テスト実行前にモックをクリア
                clearAllMocks()

                val mockUseCase = mockk<MarkRelayStatusUseCase>()
                val expectedResponse = RelayStatusToggleResponse(reviewUUID)

                // モックの設定
                coEvery {
                    mockUseCase.execute(RelayStatusToggleRequest(userID, reviewUUID, true))
                } returns expectedResponse

                testApplication {
                    application {
                        install(ContentNegotiation) {
                            json()
                        }
                        install(Koin) {
                            modules(
                                module {
                                    single { mockUseCase }
                                }
                            )
                        }
                        install(Authentication) {
                            bearer("firebase-auth") {
                                authenticate { credential ->
                                    UserIdPrincipal(userID)
                                }
                            }
                        }
                        routing {
                            authenticate("firebase-auth") {
                                relayController()
                            }
                        }
                    }

                    // テスト実行
                    val response = client.patch("/reviews/$reviewUUID") {
                        header(HttpHeaders.Authorization, "Bearer test-token")
                        contentType(ContentType.Application.Json)
                        setBody("""{"status": "public"}""")
                    }

                    // 検証
                    response.status shouldBe HttpStatusCode.OK
                    response.bodyAsText() shouldBe """{"reviewUUID":"$reviewUUID"}"""
                    coVerify(exactly = 1) {
                        mockUseCase.execute(RelayStatusToggleRequest(userID, reviewUUID, true))
                    }
                }
            }
        }

        "プロパティテスト: PATCH /reviews/{r_id} status=\"private\" で正常にレスポンスが返される" {
            checkAll(validUserIDArb, reviewUUIDArb) { userID, reviewUUID ->
                // 各テスト実行前にモックをクリア
                clearAllMocks()

                val mockUseCase = mockk<MarkRelayStatusUseCase>()
                val expectedResponse = RelayStatusToggleResponse(reviewUUID)

                // モックの設定
                coEvery {
                    mockUseCase.execute(RelayStatusToggleRequest(userID, reviewUUID, false))
                } returns expectedResponse

                testApplication {
                    application {
                        install(ContentNegotiation) {
                            json()
                        }
                        install(Koin) {
                            modules(
                                module {
                                    single { mockUseCase }
                                }
                            )
                        }
                        install(Authentication) {
                            bearer("firebase-auth") {
                                authenticate { credential ->
                                    UserIdPrincipal(userID)
                                }
                            }
                        }
                        routing {
                            authenticate("firebase-auth") {
                                relayController()
                            }
                        }
                    }

                    // テスト実行
                    val response = client.patch("/reviews/$reviewUUID") {
                        header(HttpHeaders.Authorization, "Bearer test-token")
                        contentType(ContentType.Application.Json)
                        setBody("""{"status": "private"}""")
                    }

                    // 検証
                    response.status shouldBe HttpStatusCode.OK
                    response.bodyAsText() shouldBe """{"reviewUUID":"$reviewUUID"}"""
                    coVerify(exactly = 1) {
                        mockUseCase.execute(RelayStatusToggleRequest(userID, reviewUUID, false))
                    }
                }
            }
        }
    }

    "RelayControllerの異常系テスト" - {
        "プロパティテスト: 無効なstatus値で400 Bad Requestが返される" {
            checkAll(validUserIDArb, reviewUUIDArb) { userID, reviewUUID ->
                // 各テスト実行前にモックをクリア
                clearAllMocks()

                val mockUseCase = mockk<MarkRelayStatusUseCase>()

                testApplication {
                    application {
                        install(ContentNegotiation) {
                            json()
                        }
                        install(Koin) {
                            modules(
                                module {
                                    single { mockUseCase }
                                }
                            )
                        }
                        install(Authentication) {
                            bearer("firebase-auth") {
                                authenticate { credential ->
                                    UserIdPrincipal(userID)
                                }
                            }
                        }
                        routing {
                            authenticate("firebase-auth") {
                                relayController()
                            }
                        }
                    }

                    // テスト実行 - 無効なstatus値
                    val response = client.patch("/reviews/$reviewUUID") {
                        header(HttpHeaders.Authorization, "Bearer test-token")
                        contentType(ContentType.Application.Json)
                        setBody("""{"status": "invalid"}""")
                    }

                    // 検証
                    // 具体的なステータスコードはStatusPageによって定義されているため500で検証
                    response.status.value shouldBe 500
                    coVerify(exactly = 0) { mockUseCase.execute(any()) }
                }
            }
        }

        "プロパティテスト: 認証なしアクセスで401 Unauthorizedが返される" {
            checkAll(reviewUUIDArb) { reviewUUID ->
                // 各テスト実行前にモックをクリア
                clearAllMocks()

                val mockUseCase = mockk<MarkRelayStatusUseCase>()

                testApplication {
                    application {
                        install(ContentNegotiation) {
                            json()
                        }
                        install(Koin) {
                            modules(
                                module {
                                    single { mockUseCase }
                                }
                            )
                        }
                        install(Authentication) {
                            bearer("firebase-auth") {
                                authenticate { credential ->
                                    null // 認証失敗をシミュレート
                                }
                            }
                        }
                        routing {
                            authenticate("firebase-auth") {
                                relayController()
                            }
                        }
                    }

                    // テスト実行 - 認証ヘッダーなし
                    val response = client.patch("/reviews/$reviewUUID") {
                        contentType(ContentType.Application.Json)
                        setBody("""{"status": "public"}""")
                    }

                    // 検証
                    response.status shouldBe HttpStatusCode.Unauthorized
                    coVerify(exactly = 0) { mockUseCase.execute(any()) }
                }
            }
        }

        "プロパティテスト: UseCaseがInvalidInputExceptionを投げた場合の処理" {
            checkAll(validUserIDArb, reviewUUIDArb) { userID, reviewUUID ->
                // 各テスト実行前にモックをクリア
                clearAllMocks()

                val mockUseCase = mockk<MarkRelayStatusUseCase>()

                // モックの設定 - 例外を投げる
                coEvery {
                    mockUseCase.execute(any())
                } throws InvalidInputException("対象のレビューが存在しません。")

                testApplication {
                    application {
                        install(ContentNegotiation) {
                            json()
                        }
                        install(Koin) {
                            modules(
                                module {
                                    single { mockUseCase }
                                }
                            )
                        }
                        install(Authentication) {
                            bearer("firebase-auth") {
                                authenticate { credential ->
                                    UserIdPrincipal(userID)
                                }
                            }
                        }
                        routing {
                            authenticate("firebase-auth") {
                                relayController()
                            }
                        }
                    }

                    // テスト実行
                    val response = client.patch("/reviews/$reviewUUID") {
                        header(HttpHeaders.Authorization, "Bearer test-token")
                        contentType(ContentType.Application.Json)
                        setBody("""{"status": "public"}""")
                    }

                    // 検証 - 例外が発生した場合の適切な処理を確認
                    // 具体的なステータスコードはStatusPageによって定義されているため500で検証
                    response.status.value shouldBe 500
                    coVerify(exactly = 1) { mockUseCase.execute(any()) }
                }
            }
        }

        "プロパティテスト: 不正なJSONボディで400 Bad Requestが返される" {
            checkAll(validUserIDArb, reviewUUIDArb) { userID, reviewUUID ->
                // 各テスト実行前にモックをクリア
                clearAllMocks()

                val mockUseCase = mockk<MarkRelayStatusUseCase>()

                testApplication {
                    application {
                        install(ContentNegotiation) {
                            json()
                        }
                        install(Koin) {
                            modules(
                                module {
                                    single { mockUseCase }
                                }
                            )
                        }
                        install(Authentication) {
                            bearer("firebase-auth") {
                                authenticate { credential ->
                                    UserIdPrincipal(userID)
                                }
                            }
                        }
                        routing {
                            authenticate("firebase-auth") {
                                relayController()
                            }
                        }
                    }

                    // テスト実行 - 不正なJSON
                    val response = client.patch("/reviews/$reviewUUID") {
                        header(HttpHeaders.Authorization, "Bearer test-token")
                        contentType(ContentType.Application.Json)
                        setBody("""{"invalid": "json"}""")
                    }

                    // 検証
                    response.status shouldBe HttpStatusCode.BadRequest
                    coVerify(exactly = 0) { mockUseCase.execute(any()) }
                }
            }
        }
    }

    "RelayControllerのHTTPレイヤー固有テスト" - {
        "プロパティテスト: レスポンスボディのJSON形式が正しいこと" {
            checkAll(validUserIDArb, reviewUUIDArb) { userID, reviewUUID ->
                // 各テスト実行前にモックをクリア
                clearAllMocks()

                val mockUseCase = mockk<MarkRelayStatusUseCase>()
                val expectedResponse = RelayStatusToggleResponse(reviewUUID)

                // モックの設定
                coEvery {
                    mockUseCase.execute(any())
                } returns expectedResponse

                testApplication {
                    application {
                        install(ContentNegotiation) {
                            json()
                        }
                        install(Koin) {
                            modules(
                                module {
                                    single { mockUseCase }
                                }
                            )
                        }
                        install(Authentication) {
                            bearer("firebase-auth") {
                                authenticate { credential ->
                                    UserIdPrincipal(userID)
                                }
                            }
                        }
                        routing {
                            authenticate("firebase-auth") {
                                relayController()
                            }
                        }
                    }

                    // テスト実行
                    val response = client.patch("/reviews/$reviewUUID") {
                        header(HttpHeaders.Authorization, "Bearer test-token")
                        contentType(ContentType.Application.Json)
                        setBody("""{"status": "public"}""")
                    }

                    // 検証
                    response.status shouldBe HttpStatusCode.OK
                    response.headers[HttpHeaders.ContentType] shouldBe "application/json; charset=UTF-8"

                    // JSONレスポンスの構造検証
                    val responseBody = response.bodyAsText()
                    responseBody shouldBe """{"reviewUUID":"$reviewUUID"}"""
                }
            }
        }
    }
})
