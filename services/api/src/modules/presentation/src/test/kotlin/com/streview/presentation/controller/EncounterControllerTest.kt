package com.streview.presentation.controller

import com.streview.application.usecases.encounters.EncounterUseCase
import com.streview.application.usecases.encounters.dto.EncounterResponse
import com.streview.domain.exceptions.InvalidInputException
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe
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

class EncounterControllerTest : FreeSpec({

    beforeEach { if (GlobalContext.getOrNull() != null) stopKoin() }
    afterEach { if (GlobalContext.getOrNull() != null) stopKoin() }

    "正常系" - {
        "有効なリクエストで200レスポンス" {
            clearAllMocks()
            val mockUseCase = mockk<EncounterUseCase>()
            coEvery { mockUseCase.execute(any()) } returns EncounterResponse(2)

            testApplication {
                application {
                    install(ContentNegotiation) { json() }
                    install(Koin) { modules(module { single { mockUseCase } }) }
                    install(Authentication) {
                        bearer("firebase-auth") { authenticate { UserIdPrincipal("user123") } }
                    }
                    routing { authenticate("firebase-auth") { encounterController() } }
                }

                val response = client.post("/encounters") {
                    header(HttpHeaders.Authorization, "Bearer test-token")
                    contentType(ContentType.Application.Json)
                    setBody("""{"encounters": [{"date": "2024-01-15", "encounter": ["enc1", "enc2"]}]}""")
                }

                response.status shouldBe HttpStatusCode.OK
                response.bodyAsText() shouldBe """{"encounterCount":2}"""
                coVerify(exactly = 1) { mockUseCase.execute(any()) }
            }
        }

        "空のencounterリストで正常処理" {
            clearAllMocks()
            val mockUseCase = mockk<EncounterUseCase>()
            coEvery { mockUseCase.execute(any()) } returns EncounterResponse(0)

            testApplication {
                application {
                    install(ContentNegotiation) { json() }
                    install(Koin) { modules(module { single { mockUseCase } }) }
                    install(Authentication) {
                        bearer("firebase-auth") { authenticate { UserIdPrincipal("user123") } }
                    }
                    routing { authenticate("firebase-auth") { encounterController() } }
                }

                val response = client.post("/encounters") {
                    header(HttpHeaders.Authorization, "Bearer test-token")
                    contentType(ContentType.Application.Json)
                    setBody("""{"encounters": []}""")
                }

                response.status shouldBe HttpStatusCode.OK
                response.bodyAsText() shouldBe """{"encounterCount":0}"""
            }
        }
    }

    "異常系" - {
        "認証なしで401エラー" {
            clearAllMocks()
            val mockUseCase = mockk<EncounterUseCase>()

            testApplication {
                application {
                    install(ContentNegotiation) { json() }
                    install(Koin) { modules(module { single { mockUseCase } }) }
                    install(Authentication) {
                        bearer("firebase-auth") { authenticate { null } }
                    }
                    routing { authenticate("firebase-auth") { encounterController() } }
                }

                val response = client.post("/encounters") {
                    contentType(ContentType.Application.Json)
                    setBody("""{"encounters": []}""")
                }

                response.status shouldBe HttpStatusCode.Unauthorized
                coVerify(exactly = 0) { mockUseCase.execute(any()) }
            }
        }

        "不正JSONで400エラー" {
            clearAllMocks()
            val mockUseCase = mockk<EncounterUseCase>()

            testApplication {
                application {
                    install(ContentNegotiation) { json() }
                    install(Koin) { modules(module { single { mockUseCase } }) }
                    install(Authentication) {
                        bearer("firebase-auth") { authenticate { UserIdPrincipal("user123") } }
                    }
                    routing { authenticate("firebase-auth") { encounterController() } }
                }

                val response = client.post("/encounters") {
                    header(HttpHeaders.Authorization, "Bearer test-token")
                    contentType(ContentType.Application.Json)
                    setBody("""{"invalid": "json"}""")
                }

                response.status shouldBe HttpStatusCode.BadRequest
                coVerify(exactly = 0) { mockUseCase.execute(any()) }
            }
        }

        "UseCase例外で500エラー" {
            clearAllMocks()
            val mockUseCase = mockk<EncounterUseCase>()
            coEvery { mockUseCase.execute(any()) } throws InvalidInputException("暗号化エラー")

            testApplication {
                application {
                    install(ContentNegotiation) { json() }
                    install(Koin) { modules(module { single { mockUseCase } }) }
                    install(Authentication) {
                        bearer("firebase-auth") { authenticate { UserIdPrincipal("user123") } }
                    }
                    routing { authenticate("firebase-auth") { encounterController() } }
                }

                val response = client.post("/encounters") {
                    header(HttpHeaders.Authorization, "Bearer test-token")
                    contentType(ContentType.Application.Json)
                    setBody("""{"encounters": [{"date": "2024-01-15", "encounter": ["enc1"]}]}""")
                }

                response.status.value shouldBe 500
                coVerify(exactly = 1) { mockUseCase.execute(any()) }
            }
        }
    }
})
