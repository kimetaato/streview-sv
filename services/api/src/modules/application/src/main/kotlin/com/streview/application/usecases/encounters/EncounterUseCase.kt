package com.streview.application.usecases.encounters

import com.github.michaelbull.result.fold
import com.streview.application.services.EncounterDecryptionService
import com.streview.application.usecases.UseCase
import com.streview.common.dto.encounters.EncounterRequest
import com.streview.common.dto.encounters.EncounterResponse
import com.streview.domain.commons.UserID
import com.streview.domain.commons.event.EventBus
import com.streview.domain.encounters.Encounter
import com.streview.domain.encounters.EncounterRepository
import com.streview.domain.exceptions.DuplicateEncounterException
import org.jetbrains.exposed.v1.r2dbc.transactions.suspendTransaction

class EncounterUseCase(
    private val encounterRepository: EncounterRepository,
    private val encounterDecryptionService: EncounterDecryptionService
) : UseCase<EncounterRequest, EncounterResponse> {
    override suspend fun execute(input: EncounterRequest): EncounterResponse =
        suspendTransaction {
            var encounterCount = 0
            // 日付ごとのエンティティで処理を行う
            input.encounters.forEach { encounterData ->
                encounterRepository.findByUserIDAndEncounterDate(input.userID, encounterData.date).fold(
                    success = { existingEncounter ->
                        // 既存のEncounterがあれば使用、なければ新規作成
                        val encounter = existingEncounter ?: Encounter.create(input.userID, encounterData.date)

                        // すれ違いを追加
                        encounterData.encounter.forEach { encounterID ->
                            // 暗号化されたencounterIDから実際のUserIDを抽出
//                            val actualUserID = dS.extractUserID(encryptedEncounterID)
                            val encounterID = UserID(encounterID)
                            try {
                                encounter.add(encounterID)
                                encounterCount++
                            } catch (_: DuplicateEncounterException) {}
                        }

                        // 永続化
                        encounterRepository.save(encounter)

                        // イベント発行
                        encounter.domainEvents.map { event ->
                            EventBus.publish(event)
                        }
                    },
                    failure = {
                        // エラーハンドリング（必要に応じて実装）
                        throw it
                    }
                )
            }

            // レビューの受信があればtrueを返す
            EncounterResponse(encounterCount)
        }
}
