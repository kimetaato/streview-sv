package com.streview.infrastructure.database.encounters

import com.streview.domain.encounters.Encounter
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.map
import io.kotest.property.arbitrary.set
import io.kotest.property.arbitrary.stringPattern
import io.kotest.property.checkAll
import kotlinx.datetime.LocalDate
import org.jetbrains.exposed.v1.r2dbc.transactions.suspendTransaction

class EncounterRepositoryImplTest : FreeSpec({

    val repository = EncounterRepositoryImpl()

    val validUserIDArb = Arb.stringPattern("[a-zA-Z0-9]{28}")
    val validEncounterDateArb = Arb.int(1900..2024).map { year ->
        val month = (1..12).random()
        val day = (1..28).random()
        LocalDate(year, month, day)
    }

    /**
     * Actor IDとEncounter ID間で重複を防止するためのID生成関数
     * @param encounterIDsCount IntRange Encounter IDの生成すうに制限を持たせる。デフォルトでは1件
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

    "EncounterRepositoryImplの統合テスト" - {
        "saveメソッドのプロパティテスト" - {
            "プロパティテスト: 任意のencounterを保存できること" {

                checkAll(combinedArb(), validEncounterDateArb) { (actorID, encounterIDs), encounterDate ->
                    suspendTransaction {
                        val encounter = Encounter.factory(actorID, encounterDate, encounterIDs)

                        val savedEncounter = repository.save(encounter)

                        savedEncounter.actorID.value shouldBe actorID
                        savedEncounter.encounterDate.value shouldBe encounterDate
                        savedEncounter.encounterIDs shouldHaveSize encounterIDs.size

                        rollback()
                    }
                }
            }
        }

        "findByIDメソッドのプロパティテスト" - {
            "プロパティテスト: 保存したencounterを取得できること" {
                checkAll(combinedArb(), validEncounterDateArb) { (actorID, encounterIDs), encounterDate ->
                    suspendTransaction {
                        val originalEncounter = Encounter.factory(actorID, encounterDate, encounterIDs)

                        repository.save(originalEncounter)

                        val foundEncounter = repository.findByID(actorID, encounterDate)

                        foundEncounter.actorID.value shouldBe actorID
                        foundEncounter.encounterDate.value shouldBe encounterDate
                        foundEncounter.encounterIDs shouldHaveSize encounterIDs.size
                        foundEncounter.encounterIDs.map { it.value }.toSet() shouldBe encounterIDs.toSet()

                        rollback()
                    }
                }
            }
        }

        "save-findサイクルのプロパティテスト" - {
            "プロパティテスト: 保存と取得の整合性が保たれること" {
                checkAll(combinedArb(), validEncounterDateArb) { (actorID, encounterIDs), encounterDate ->

                    suspendTransaction {
                        val originalEncounter = Encounter.factory(actorID, encounterDate, encounterIDs)

                        val savedEncounter = repository.save(originalEncounter)
                        val retrievedEncounter = repository.findByID(actorID, encounterDate)

                        savedEncounter.actorID shouldBe retrievedEncounter.actorID
                        savedEncounter.encounterDate shouldBe retrievedEncounter.encounterDate
                        savedEncounter.encounterIDs shouldHaveSize retrievedEncounter.encounterIDs.size
                        savedEncounter.encounterIDs.toSet() shouldBe retrievedEncounter.encounterIDs.toSet()

                        rollback()
                    }
                }
            }
        }
    }
})