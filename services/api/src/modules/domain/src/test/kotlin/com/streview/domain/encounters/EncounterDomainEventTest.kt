package com.streview.domain.encounters

import com.streview.domain.commons.UserID
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.map
import io.kotest.property.arbitrary.set
import io.kotest.property.arbitrary.stringPattern
import io.kotest.property.checkAll
import kotlinx.datetime.LocalDate

class EncounterDomainEventTest : FreeSpec({
    /**
     * actor IDとencounter IDが重複しないことを保証するArb
     */
    fun combinedArb(encounterIDsCount: IntRange = 1..1) = Arb.set(
        Arb.stringPattern("[a-zA-Z0-9]{28}"),
        range = (encounterIDsCount.first + 1..encounterIDsCount.last + 1)
    )  // 最低2個生成
        .map { userIDs ->
            val userIDList = userIDs.toList()
            val actorID = userIDList.first()         // 1番目を取得
            val encounterIDs = userIDList.drop(1)    // 残りを配列に
            Pair(actorID, encounterIDs)
        }

    val validEncounterDateArb = Arb.int(1900..2024).map { year ->
        val month = (1..12).random()
        val day = (1..28).random()
        LocalDate(year, month, day)
    }
    "Domain Eventの登録テスト" - {
        "正常系" - {
            "プロパティテスト: 1件のすれ違いが正しく行える" {
                checkAll(combinedArb(), validEncounterDateArb) { (actorID, encounterIDs), encounterDate ->
                    val encounter = Encounter.factory(actorID, encounterDate)

                    // 追加
                    encounter.add(UserID(encounterIDs.first()))

                    encounter.domainEvents.size shouldBe 1
                    encounter.domainEvents.first() shouldBe EncounterAddDomainEvent(
                        actorID = UserID(actorID),
                        encounterDate = EncounterDate(encounterDate),
                        encounterID = UserID(encounterIDs.first())
                    )
                }
            }

            "プロパティテスト: 複数件のすれ違いが正しく行える" {
                checkAll(
                    combinedArb(encounterIDsCount = 2..3),
                    validEncounterDateArb
                ) { (actorID, encounterIDs), encounterDate ->
                    val encounter = Encounter.factory(actorID, encounterDate)

                    encounterIDs.forEach { encounterID ->
                        encounter.add(UserID(encounterID))
                    }

                    encounter.domainEvents.size shouldBe encounterIDs.size
                    encounter.domainEvents.forEachIndexed { index, domainEvent ->
                        domainEvent shouldBe EncounterAddDomainEvent(
                            actorID = UserID(actorID),
                            encounterDate = EncounterDate(encounterDate),
                            encounterID = UserID(encounterIDs[index])
                        )
                    }
                }
            }
        }
    }
})