package com.streview.domain.encounters

import com.streview.domain.commons.UserID
import com.streview.domain.exceptions.DuplicateEncounterException
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
        "ファクトリーメソッドで新しいEncounterを作成する（encounterIDsなし）" - {
            "プロパティテスト: 任意のactorIDと日付で正しく作成される" {
                checkAll(validUserIDArb, validEncounterDateArb) { actorID, encounterDate ->
                    val encounter = Encounter.factory(actorID, encounterDate)

                    encounter.actorID.value shouldBe actorID
                    encounter.encounterDate.value shouldBe encounterDate
                    encounter.encounterIDs.shouldBeEmpty()
                }
            }
        }

        "ファクトリーメソッドで新しいEncounterを作成する（encounterIDsあり）" - {
            "プロパティテスト: 任意のactorIDと日付とencounterIDsで正しく作成される" {
                val encounterIDsArb = Arb.list(validUserIDArb, range = 0..5)

                checkAll(
                    validUserIDArb,
                    validEncounterDateArb,
                    encounterIDsArb
                ) { actorID, encounterDate, encounterIDs ->
                    val encounter = Encounter.factory(actorID, encounterDate, encounterIDs)

                    encounter.actorID.value shouldBe actorID
                    encounter.encounterDate.value shouldBe encounterDate
                    encounter.encounterIDs shouldHaveSize encounterIDs.size
                    encounterIDs.forEach { id ->
                        encounter.encounterIDs shouldContain UserID(id)
                    }
                }
            }
        }

        "addメソッドで新しいユーザーを追加する" - {
            "プロパティテスト: 異なるユーザーIDを追加すると正しく追加される" {
                checkAll(validUserIDArb, validUserIDArb, validEncounterDateArb) { actorID, newUserID, encounterDate ->
                    val encounter = Encounter.factory(actorID, encounterDate)
                    if (actorID != newUserID) {
                        val updatedEncounter = encounter.add(UserID(newUserID))

                        updatedEncounter.encounterIDs shouldHaveSize 1
                        updatedEncounter.encounterIDs shouldContain UserID(newUserID)
                        updatedEncounter shouldBe encounter
                    }
                }
            }
        }

        "addメソッドで自分自身を追加しようとする" - {
            "プロパティテスト: 自分自身を追加してもencounterIDsは変更されない" {
                checkAll(validUserIDArb, validEncounterDateArb) { actorID, encounterDate ->
                    val encounter = Encounter.factory(actorID, encounterDate)
                    val updatedEncounter = encounter.add(UserID(actorID))

                    updatedEncounter.encounterIDs.shouldBeEmpty()
                    updatedEncounter shouldBe encounter
                }
            }
        }

        "addメソッドで既に追加済みのユーザーを追加しようとする" - {
            "プロパティテスト: 重複ユーザーを追加するとDuplicateEncounterExceptionが発生する" {
                checkAll(
                    validUserIDArb,
                    validUserIDArb,
                    validEncounterDateArb
                ) { actorID, duplicateUserID, encounterDate ->
                    if (actorID != duplicateUserID) {
                        val encounter = Encounter.factory(actorID, encounterDate, listOf(duplicateUserID))

                        shouldThrow<DuplicateEncounterException> {
                            encounter.add(UserID(duplicateUserID))
                        }
                    }
                }
            }
        }

        "encounterIDsプロパティにアクセスする" - {
            "プロパティテスト: immutableなリストが返される" {
                val encounterIDsArb = Arb.list(validUserIDArb, range = 1..3)

                checkAll(
                    validUserIDArb,
                    validEncounterDateArb,
                    encounterIDsArb
                ) { actorID, encounterDate, encounterIDs ->
                    val encounter = Encounter.factory(actorID, encounterDate, encounterIDs)
                    val returnedList = encounter.encounterIDs

                    returnedList shouldHaveSize encounterIDs.size
                    encounterIDs.forEach { id ->
                        returnedList shouldContain UserID(id)
                    }
                }
            }
        }

        "具体的なテストケース" {
            val actorID = "abcdefghijklmnopqrstuvwxyz12"
            val encounterDate = LocalDate(2024, 1, 1)

            val encounter = Encounter.factory(actorID, encounterDate)

            encounter.actorID.value shouldBe actorID
            encounter.encounterDate.value shouldBe encounterDate
            encounter.encounterIDs.shouldBeEmpty()
        }
    }

    "EncounterDateの値オブジェクト" - {
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
        }

        "プロパティテスト: 未来の日付でIllegalArgumentExceptionが発生する" {
            val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
            val afterDaysArb = Arb.int(1..1000)

            checkAll(afterDaysArb) { afterDays ->
                val futureDate = today.plus(afterDays, DateTimeUnit.DAY)
                shouldThrow<IllegalArgumentException> {
                    EncounterDate(futureDate)
                }
            }
        }

        "具体的なテストケース" {
            val today = LocalDate(2024, 7, 12)
            val encounterDate = EncounterDate(today)
            encounterDate.value shouldBe today
        }
    }
})