package com.streview.infrastructure.database.encounters

import com.streview.domain.commons.UserID
import com.streview.domain.encounters.Encounter
import com.streview.domain.encounters.EncounterDate
import io.kotest.assertions.throwables.shouldNotThrow
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import kotlinx.datetime.LocalDate
import org.jetbrains.exposed.v1.r2dbc.transactions.suspendTransaction


internal class EncounterRepositoryImplTest : FreeSpec({
    "Encounter Repository Test" - {
        val repository = EncounterRepositoryImpl()

        val userID = UserID("testUserID123456789000000001")
        val encounterID = UserID("testUserID123456789000000002")
        val date = EncounterDate(LocalDate(2023, 1, 1))
        val encounter = Encounter.factory(userID, date, listOf(encounterID))

        "正常系" - {
            "レコード登録" - {
                shouldNotThrow<Exception> {
                    suspendTransaction {
                        repository.save(encounter)
                    }
                }
            }
            "取得" - {
                val result = suspendTransaction {
                    repository.findByID(userID, date)
                }
                result shouldNotBe null
                result.encounterIDs.size shouldBe 1
                result.encounterIDs[0].value shouldBe encounterID.value
            }
        }
    }
})