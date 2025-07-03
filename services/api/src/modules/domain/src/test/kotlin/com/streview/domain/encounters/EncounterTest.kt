package com.streview.domain.encounters

import com.streview.domain.commons.UserID
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe
import kotlinx.datetime.LocalDate

class EncounterTest : FreeSpec({
    "Value Object Test" - {
        "Encounter Date" - {
            "正常系" - {
                val date = LocalDate(2023, 1, 1)
                val encounterDate = EncounterDate(date)

                encounterDate.value shouldBe date
            }
        }
    }
    "Encounter Domain Test" - {
        val actorIDStr = "testUserID123456789000000001"
        val encounterIDStr = "testUserID123456789000000002"
        "正常系" - {
            val actorID = UserID(actorIDStr)
            val date = EncounterDate(LocalDate(2023, 1, 1))
            val encounterID = UserID(encounterIDStr)

            "新規作成" - {
                val encounter = Encounter.factory(actorID, date)

                encounter.actorID.value shouldBe actorID.value
                encounter.encounterDate shouldBe date
                encounter.encounterIDs.isEmpty() shouldBe true
            }
            "再生成" - {
                val encounterIDs = listOf(encounterID)
                val encounter = Encounter.factory(actorID, date, encounterIDs)

                encounter.actorID.value shouldBe actorID.value
                encounter.encounterDate shouldBe date
                encounter.encounterIDs shouldBe encounterIDs
            }
        }
    }
})



