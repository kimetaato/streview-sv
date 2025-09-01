package com.streview.application.usecases.encounters

import io.kotest.core.spec.style.FreeSpec

class EncounterUseCaseTest : FreeSpec({
//    val userIDArb = Arb.stringPattern("[a-zA-Z0-9]{28}")
//    val encounterDateArb = Arb.int(1900..2024).map { year ->
//        val month = (1..12).random()
//        val day = (1..28).random()
//        LocalDate(year, month, day)
//    }
//    // 複数日付の生成
//    val encounterDatesArb = Arb.set(encounterDateArb, 2..5).map { it.toList() }
//    // 複数IDの生成
//    val encounterIDsArb = Arb.set(userIDArb, 2..5).map { it.toList() }
//
//    "EncounterUseCaseの正常系テスト" - {
//        "プロパティテスト:複数の日の日にちの場合に正しく処理される" {
//            checkAll(
//                userIDArb,
//                encounterDatesArb,
//                encounterIDsArb,
//            ) { actorID, encounterDates, encounterIDs ->
//                // 各テスト実行前にモックをクリア
//                clearAllMocks()
//
//                val mockRepository = mockk<EncounterRepository>()
//                val mockDecryptionService = mockk<EncounterDecryptionService>()
//
//                /**
//                 * 日付を引数にして、Encounterエンティティのmockを作成し、Mapの形で返す
//                 */
//                val mockEncounterMap = encounterDates.associateWith { date ->
//                    val mockEncounter = mockk<Encounter>(relaxed = true)
//                    every { mockEncounter.add(any()) } just Runs
//                    mockEncounter
//                }
//
//                // 関数の定義
//                /**
//                 * 引数で指定される日付に応じて返すMockを変更する
//                 */
//                coEvery { mockRepository.findByID(actorID, any()) } answers {
//                    val date = secondArg<LocalDate>()
//                    Ok(mockEncounterMap[date])
//                }
//
//                /**
//                 * 自身のインスタンスを返す。
//                 */
//                coEvery { mockRepository.save(any()) } answers { Ok(firstArg()) }
//
//                /**
//                 * 入力されたものからUserIDを生成する
//                 */
//                every { mockDecryptionService.extractUserID(any<String>()) }
//                    returnsMany encounterIDs.map { UserID(it) }
//
//                // テスト実行
//                val useCase = EncounterUseCase(mockRepository, mockDecryptionService)
//                val encounters = encounterDates.map { date ->
//                    DailyEncounter(date, encounterIDs)
//                }
//                val request = EncounterRequest(
//                    userID = actorID,
//                    encounters = encounters
//                )
//
//                val response = useCase.execute(request)
//
//                // 検証
//                response.encounterCount shouldBe encounterDates.size * encounterIDs.size // レスポンス成功してるか
//                coVerify(exactly = encounterDates.size) { mockRepository.findByID(actorID, any()) } // 日付の数だけ
//                coVerify(exactly = encounterDates.size) { mockRepository.save(any()) } // 日付の数だけ
//                mockEncounterMap.values.forEach { mockEncounter ->
//                    coVerify(exactly = encounterIDs.size) { mockEncounter.add(any()) }
//                }
//                verify(exactly = encounterDates.size * encounterIDs.size) {
//                    mockDecryptionService.extractUserID(any())
//                } // IDの数だけ呼び出される
//            }
//        }
//    }
})
