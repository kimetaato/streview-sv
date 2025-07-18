package com.streview.application.usecases.encounters

import com.streview.application.services.EncounterDecryptionService
import com.streview.application.usecases.encounters.dto.DailyEncounter
import com.streview.application.usecases.encounters.dto.EncounterRequest
import com.streview.domain.commons.UserID
import com.streview.domain.commons.event.DomainEvent
import com.streview.domain.commons.event.EventBus
import com.streview.domain.encounters.Encounter
import com.streview.domain.encounters.EncounterRepository
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.map
import io.kotest.property.arbitrary.set
import io.kotest.property.arbitrary.stringPattern
import io.kotest.property.checkAll
import io.mockk.*
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.LocalDate


class EncounterUseCaseTest : FreeSpec({

    val userIDArb = Arb.stringPattern("[a-zA-Z0-9]{28}")
    val encounterDateArb = Arb.int(1900..2024).map { year ->
        val month = (1..12).random()
        val day = (1..28).random()
        LocalDate(year, month, day)
    }
    // 複数日付の生成
    val encounterDatesArb = Arb.set(encounterDateArb, 2..2).map { it.toList() }
    // 複数IDの生成
    val encounterIDsArb = Arb.set(userIDArb, 2..2).map { it.toList() }

    "EncounterUseCaseの正常系テスト" - {

        "プロパティテスト:複数の日の日にちの場合に正しく処理される" {
            checkAll(
                userIDArb,
                encounterDatesArb,
                encounterIDsArb,
            ) { actorID, encounterDates, encounterIDs ->
                // 各テスト実行前にモックをクリア
                clearAllMocks()
                mockkObject(EventBus)

                val mockRepository = mockk<EncounterRepository>()
                val mockDecryptionService = mockk<EncounterDecryptionService>()

                /**
                 * 日付を引数にして、Encounterエンティティのmockを作成し、Mapの形で返す
                 */
                val mockEncounterMap = encounterDates.associateWith { date ->
                    val mockEncounter = mockk<Encounter>(relaxed = true)
                    val domainEvents = mutableListOf<DomainEvent>()
                    every { mockEncounter.add(any()) } answers {
                        domainEvents.add(mockk<DomainEvent>())
                        mockEncounter
                    }
                    every { mockEncounter.domainEvents } returns domainEvents
                    mockEncounter
                }

                // 関数の定義
                /**
                 * 引数で指定される日付に応じて返すMockを変更する
                 */
                coEvery { mockRepository.findByID(actorID, any()) } answers {
                    val date = secondArg<LocalDate>()   //
                    mockEncounterMap[date]!!
                }

                /**
                 * 自身のインスタンスを返す。
                 */
                coEvery { mockRepository.save(any()) } answers { firstArg() }

                /**
                 * 入力されたものからUserIDを生成する
                 */
                every { mockDecryptionService.extractUserID(any<String>()) } returnsMany encounterIDs.map { UserID(it) }

                /**
                 * イベント発行するよ〜
                 */
                coEvery { EventBus.publish(any<DomainEvent>()) } just Runs


                // テスト実行
                val useCase = EncounterUseCase(mockRepository, mockDecryptionService)
                val encounters = encounterDates.map { date ->
                    DailyEncounter(date, encounterIDs)
                }
                val request = EncounterRequest(
                    userID = actorID,
                    encounters = encounters
                )

                val response = runBlocking { useCase.execute(request) }


                // 検証
                response.result shouldBe true   // レスポンス成功してる？
                coVerify(exactly = encounterDates.size) { mockRepository.findByID(actorID, any()) } // 日付の数だけ
                coVerify(exactly = encounterDates.size) { mockRepository.save(any()) }  // 日付の数だけ
                mockEncounterMap.values.forEach { mockEncounter ->
                    coVerify(exactly = encounterIDs.size) { mockEncounter.add(any()) }
                    mockEncounter.domainEvents.size shouldBe encounterIDs.size
                }
                verify(exactly = encounterDates.size * encounterIDs.size) { mockDecryptionService.extractUserID(any()) }   // IDの数だけ呼び出される
                coVerify(exactly = encounterDates.size * encounterIDs.size) { EventBus.publish(any<DomainEvent>()) }
            }
        }
    }
})