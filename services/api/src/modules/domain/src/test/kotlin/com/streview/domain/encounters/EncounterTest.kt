package com.streview.domain.encounters

import com.streview.domain.commons.UserID
import com.streview.domain.exceptions.DuplicateEncounterException
import com.streview.domain.exceptions.InvalidInputException
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.list
import io.kotest.property.arbitrary.map
import io.kotest.property.arbitrary.stringPattern
import io.kotest.property.checkAll
import kotlinx.datetime.*

class EncounterTest : FreeSpec({
    val validUserIDArb = Arb.stringPattern("[a-zA-Z0-9]{28}")
    val validEncounterDateArb = Arb.int(1900..2024).map { year ->
        val month = (1..12).random()
        val day = (1..28).random()
        LocalDate(year, month, day)
    }

    "Encounterのdomain model" - {
        "プロパティテスト: 任意のactorIDと日付で正しく作成される" {
            checkAll(validUserIDArb, validEncounterDateArb) { actorID, encounterDate ->
                val encounter = Encounter.create(actorID, encounterDate)

                encounter.actorID.value shouldBe actorID
                encounter.encounterDate.value shouldBe encounterDate
                encounter.encounterIDs.shouldBeEmpty()
            }
        }

        "プロパティテスト: 任意のactorIDと日付とencounterIDsで正しく作成される" {
            val encounterIDsArb = Arb.list(validUserIDArb, range = 0..5)

            checkAll(
                validUserIDArb,
                validEncounterDateArb,
                encounterIDsArb
            ) { actorID, encounterDate, encounterIDs ->
                val encounter = Encounter.reconstruct(actorID, encounterDate, encounterIDs)

                encounter.actorID.value shouldBe actorID
                encounter.encounterDate.value shouldBe encounterDate
                encounter.encounterIDs shouldHaveSize encounterIDs.size
                encounterIDs.forEach { id ->
                    encounter.encounterIDs shouldContain UserID(id)
                }
            }
        }

        "プロパティテスト: 異なるユーザーIDを追加すると正しく追加される" {
            checkAll(validUserIDArb, validUserIDArb, validEncounterDateArb) { actorID, newUserID, encounterDate ->
                val encounter = Encounter.create(actorID, encounterDate)
                if (actorID != newUserID) {
                    encounter.add(UserID(newUserID))

                    encounter.encounterIDs shouldHaveSize 1
                    encounter.encounterIDs shouldContain UserID(newUserID)
                }
            }
        }

        "プロパティテスト: 自分自身を追加してもencounterIDsは変更されない" {
            checkAll(validUserIDArb, validEncounterDateArb) { actorID, encounterDate ->
                val encounter = Encounter.create(actorID, encounterDate)

                shouldThrow<InvalidInputException> {
                    encounter.add(UserID(actorID))
                }

                encounter.encounterIDs.shouldBeEmpty()
            }
        }

        "プロパティテスト: 重複ユーザーを追加するとDuplicateEncounterExceptionが発生する" {
            checkAll(
                validUserIDArb,
                validUserIDArb,
                validEncounterDateArb
            ) { actorID, duplicateUserID, encounterDate ->
                if (actorID != duplicateUserID) {
                    val encounter = Encounter.reconstruct(actorID, encounterDate, listOf(duplicateUserID))

                    shouldThrow<DuplicateEncounterException> {
                        encounter.add(UserID(duplicateUserID))
                    }
                }
            }
        }

        "プロパティテスト: immutableなリストが返される" {
            val encounterIDsArb = Arb.list(validUserIDArb, range = 1..3)

            checkAll(
                validUserIDArb,
                validEncounterDateArb,
                encounterIDsArb
            ) { actorID, encounterDate, encounterIDs ->
                val encounter = Encounter.reconstruct(actorID, encounterDate, encounterIDs)
                val returnedList = encounter.encounterIDs

                returnedList shouldHaveSize encounterIDs.size
                encounterIDs.forEach { id ->
                    returnedList shouldContain UserID(id)
                }
            }
        }
    }

    "プロパティテスト: 過去と現在の日付で正常に作成される" {
        val pastAndPresentDateArb = Arb.int(1900..2024).map { year ->
            val month = (1..12).random()
            val day = (1..28).random()
            LocalDate(year, month, day)
        }

        checkAll(pastAndPresentDateArb) { date ->
            val encounterDate = EncounterDate(date)
            encounterDate.value shouldBe date
        }

        val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
        val afterDaysArb = Arb.int(1..1000)

        checkAll(afterDaysArb) { afterDays ->
            val futureDate = today.plus(afterDays, DateTimeUnit.DAY)
            shouldThrow<IllegalArgumentException> {
                EncounterDate(futureDate)
            }
        }
    }
})
