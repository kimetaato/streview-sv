package com.streview.application.usecases.encounters

import com.streview.application.services.EncounterDecryptionService
import com.streview.application.usecases.encounters.dto.EncounterRequest
import com.streview.domain.commons.UserID
import com.streview.domain.commons.event.EventBus
import com.streview.domain.commons.event.EventHandler
import com.streview.domain.encounters.Encounter
import com.streview.domain.encounters.EncounterAddDomainEvent
import com.streview.domain.encounters.EncounterDate
import com.streview.domain.encounters.EncounterRepository
import com.streview.domain.exceptions.DuplicateEncounterException
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe
import kotlinx.datetime.LocalDate

class EncounterRegisterUseCaseTest: FreeSpec({
    
    // テスト用のモックDecryptionService
    class MockEncounterDecryptionService : EncounterDecryptionService("test-key-32-characters-long-12") {
        override fun extractUserID(encryptedEncounterID: String): UserID {
            // テスト用の簡単な実装: 直接UserIDとして返す
            return UserID(encryptedEncounterID)
        }
    }
    
    // テスト用のモックリポジトリ
    class MockEncounterRepository : EncounterRepository {
        private val encounters = mutableMapOf<Pair<String, String>, Encounter>()
        
        override suspend fun findByID(userID: UserID, encounterDate: EncounterDate): Encounter {
            val key = Pair(userID.value, encounterDate.value.toString())
            return encounters[key] ?: Encounter.factory(userID, encounterDate)
        }
        
        override suspend fun save(encounter: Encounter): Encounter {
            val key = Pair(encounter.actorID.value, encounter.encounterDate.value.toString())
            encounters[key] = encounter
            return encounter
        }
        
    }
    
    // テスト用のイベントハンドラー
    class TestEventHandler : EventHandler<EncounterAddDomainEvent> {
        val receivedEvents = mutableListOf<EncounterAddDomainEvent>()
        
        override suspend fun handle(event: EncounterAddDomainEvent) {
            receivedEvents.add(event)
        }
    }
    
    "正常系" - {
        "新しいすれ違いを登録できる" {
            // Arrange
            val mockRepository = MockEncounterRepository()
            val useCase = EncounterUseCase(mockRepository, MockEncounterDecryptionService())
            
            val request = EncounterRequest(
                userID = "testUserID123456789000000001",
                encounters = mapOf(
                    "2023-01-01" to listOf("testUserID123456789000000002")
                )
            )
            
            // Act
            val response = useCase.execute(request)
            
            // Assert
            response.result shouldBe true
        }
        
        "複数のすれ違いを登録できる" {
            // Arrange
            val mockRepository = MockEncounterRepository()
            val useCase = EncounterUseCase(mockRepository, MockEncounterDecryptionService())
            
            val request = EncounterRequest(
                userID = "testUserID123456789000000001",
                encounters = mapOf(
                    "2023-01-01" to listOf("testUserID123456789000000002", "testUserID123456789000000003")
                )
            )
            
            // Act
            val response = useCase.execute(request)
            
            // Assert
            response.result shouldBe true
        }
        
        "複数日のすれ違いを登録できる" {
            // Arrange
            val mockRepository = MockEncounterRepository()
            val useCase = EncounterUseCase(mockRepository, MockEncounterDecryptionService())
            
            val request = EncounterRequest(
                userID = "testUserID123456789000000001",
                encounters = mapOf(
                    "2023-01-01" to listOf("testUserID123456789000000002"),
                    "2023-01-02" to listOf("testUserID123456789000000003")
                )
            )
            
            // Act
            val response = useCase.execute(request)
            
            // Assert
            response.result shouldBe true
        }
        
        "すれ違いが空の場合はfalseを返す" {
            // Arrange
            val mockRepository = MockEncounterRepository()
            val useCase = EncounterUseCase(mockRepository, MockEncounterDecryptionService())
            
            val request = EncounterRequest(
                userID = "testUserID123456789000000001",
                encounters = emptyMap()
            )
            
            // Act
            val response = useCase.execute(request)
            
            // Assert
            response.result shouldBe false
        }
    }
    
    "異常系" - {
        "重複したすれ違いを登録しようとする場合例外が発生する" {
            // Arrange
            val mockRepository = MockEncounterRepository()
            val mockDecryptionService = MockEncounterDecryptionService()
            val useCase = EncounterUseCase(mockRepository, mockDecryptionService)
            
            val userID = UserID("testUserID123456789000000001")
            val encounterDate = EncounterDate(LocalDate.parse("2023-01-01"))
            val existingEncounter = Encounter.factory(userID, encounterDate)
            existingEncounter.add(UserID("testUserID123456789000000002"))
            mockRepository.save(existingEncounter)
            
            val request = EncounterRequest(
                userID = "testUserID123456789000000001",
                encounters = mapOf(
                    "2023-01-01" to listOf("testUserID123456789000000002") // 既に登録済み
                )
            )
            
            // Act & Assert
            shouldThrow<DuplicateEncounterException> {
                useCase.execute(request)
            }
        }
    }
    
    "イベント発行のテスト" - {
        "ドメインイベントのパラメータが正しく設定される" {
            // Arrange
            val mockRepository = MockEncounterRepository()
            val useCase = EncounterUseCase(mockRepository, MockEncounterDecryptionService())
            val testHandler = TestEventHandler()
            
            // イベントハンドラーを登録
            EventBus.subscribe(EncounterAddDomainEvent::class.java, testHandler)
            
            // UserIDの制約に従った有効な28桁の英数字
            val actorUserID = "abcd1234efgh5678ijkl9012mnop"
            val encounterUserID = "wxyz0987zyxw8765utsrq543nmlk"
            val encounterDateStr = "2023-01-01" // 過去の日付
            
            val request = EncounterRequest(
                userID = actorUserID,
                encounters = mapOf(
                    encounterDateStr to listOf(encounterUserID)
                )
            )
            
            // Act
            val response = useCase.execute(request)
            
            // Assert
            response.result shouldBe true
            
            // イベントが発行されたかを確認
            testHandler.receivedEvents.size shouldBe 1
            
            // イベントのパラメータを詳細に検証
            val event = testHandler.receivedEvents[0]
            event.actorID.value shouldBe actorUserID
            event.encounterID.value shouldBe encounterUserID
            event.encounterDate.value shouldBe LocalDate.parse(encounterDateStr)
            
            // VOの制約に従っていることを確認
            event.actorID.value.length shouldBe 28
            event.encounterID.value.length shouldBe 28
            event.encounterDate.value shouldBe LocalDate.parse(encounterDateStr)
        }
        
/*
        "EncounterAddDomainEventのパラメータ詳細検証" {
            // このテストは一時的にコメントアウト - UserID制約の問題を調査中
        }
        */
        
        "自分自身とのすれ違いではイベントが発行されない" {
            // Arrange
            val mockRepository = MockEncounterRepository()
            val useCase = EncounterUseCase(mockRepository, MockEncounterDecryptionService())
            val testHandler = TestEventHandler()
            
            EventBus.subscribe(EncounterAddDomainEvent::class.java, testHandler)
            
            // 有効な28桁のUserID（自分自身）
            val actorUserID = "selfencounter123456789012345"
            val encounterDateStr = "2023-01-01"
            
            val request = EncounterRequest(
                userID = actorUserID,
                encounters = mapOf(
                    encounterDateStr to listOf(actorUserID) // 自分自身とのすれ違い
                )
            )
            
            // Act
            val response = useCase.execute(request)
            
            // Assert
            response.result shouldBe true
            
            // 自分自身とのすれ違いではイベントが発行されないことを確認
            testHandler.receivedEvents.size shouldBe 0
        }
        
        "複数のすれ違いで複数のイベントが発行される" {
            // Arrange
            val mockRepository = MockEncounterRepository()
            val useCase = EncounterUseCase(mockRepository, MockEncounterDecryptionService())
            val testHandler = TestEventHandler()
            
            // イベントハンドラーを登録
            EventBus.subscribe(EncounterAddDomainEvent::class.java, testHandler)
            
            val actorUserID = "testUserID123456789000000001"
            val encounterUserID1 = "testUserID123456789000000002"
            val encounterUserID2 = "testUserID123456789000000003"
            val encounterDateStr = "2023-01-01"
            
            val request = EncounterRequest(
                userID = actorUserID,
                encounters = mapOf(
                    encounterDateStr to listOf(encounterUserID1, encounterUserID2)
                )
            )
            
            // Act
            val response = useCase.execute(request)
            
            // Assert
            response.result shouldBe true
            
            // 2つのイベントが発行されたかを確認
            testHandler.receivedEvents.size shouldBe 2
            
            // 1つ目のイベントの検証
            val event1 = testHandler.receivedEvents[0]
            event1.actorID.value shouldBe actorUserID
            event1.encounterID.value shouldBe encounterUserID1
            event1.encounterDate.value shouldBe LocalDate.parse(encounterDateStr)
            
            // 2つ目のイベントの検証
            val event2 = testHandler.receivedEvents[1]
            event2.actorID.value shouldBe actorUserID
            event2.encounterID.value shouldBe encounterUserID2
            event2.encounterDate.value shouldBe LocalDate.parse(encounterDateStr)
        }
        
        "複数日のすれ違いで日付別にイベントが発行される" {
            // Arrange
            val mockRepository = MockEncounterRepository()
            val useCase = EncounterUseCase(mockRepository, MockEncounterDecryptionService())
            val testHandler = TestEventHandler()
            
            // イベントハンドラーを登録
            EventBus.subscribe(EncounterAddDomainEvent::class.java, testHandler)
            
            val actorUserID = "testUserID123456789000000001"
            val encounterUserID1 = "testUserID123456789000000002"
            val encounterUserID2 = "testUserID123456789000000003"
            val encounterDate1 = "2023-01-01"
            val encounterDate2 = "2023-01-02"
            
            val request = EncounterRequest(
                userID = actorUserID,
                encounters = mapOf(
                    encounterDate1 to listOf(encounterUserID1),
                    encounterDate2 to listOf(encounterUserID2)
                )
            )
            
            // Act
            val response = useCase.execute(request)
            
            // Assert
            response.result shouldBe true
            
            // 2つのイベントが発行されたかを確認
            testHandler.receivedEvents.size shouldBe 2
            
            // 日付順でソートして検証（マップの順序が保証されないため）
            val sortedEvents = testHandler.receivedEvents.sortedBy { it.encounterDate.value.toString() }
            
            // 1つ目のイベント（2023-01-01）の検証
            val event1 = sortedEvents[0]
            event1.actorID.value shouldBe actorUserID
            event1.encounterID.value shouldBe encounterUserID1
            event1.encounterDate.value shouldBe LocalDate.parse(encounterDate1)
            
            // 2つ目のイベント（2023-01-02）の検証
            val event2 = sortedEvents[1]
            event2.actorID.value shouldBe actorUserID
            event2.encounterID.value shouldBe encounterUserID2
            event2.encounterDate.value shouldBe LocalDate.parse(encounterDate2)
        }
    }
})