package com.streview.application.usecases.encounters

import com.github.michaelbull.result.fold
import com.streview.application.services.EncounterDecryptionService
import com.streview.application.usecases.UseCase
import com.streview.application.usecases.encounters.dto.EncounterRequest
import com.streview.application.usecases.encounters.dto.EncounterResponse
import com.streview.domain.encounters.Encounter
import com.streview.domain.encounters.EncounterRepository
import com.streview.domain.exceptions.DuplicateEncounterException

class EncounterUseCase(
    val eR: EncounterRepository,
    private val dS: EncounterDecryptionService
) : UseCase<EncounterRequest, EncounterResponse> {
    override suspend fun execute(input: EncounterRequest): EncounterResponse {
        var encounterCount = 0
        // 日付ごとのエンティティで処理を行う
        input.encounters.forEach { encounterData ->
            eR.findByID(input.userID, encounterData.date).fold(
                success = { existingEncounter ->
                    // 既存のEncounterがあれば使用、なければ新規作成
                    val encounter = existingEncounter ?: Encounter.factory(input.userID, encounterData.date)

                    // すれ違いを追加
                    encounterData.encounter.forEach { encryptedEncounterID ->
                        // 暗号化されたencounterIDから実際のUserIDを抽出
                        val actualUserID = dS.extractUserID(encryptedEncounterID)
                        try {
                            encounter.add(actualUserID)
                            encounterCount++
                        } catch (_: DuplicateEncounterException) {}
                    }

                    // 永続化
                    eR.save(encounter)
                },
                failure = {
                    // エラーハンドリング（必要に応じて実装）
                    throw it
                }
            )
        }

        // レビューの受信があればtrueを返す
        return EncounterResponse(encounterCount)
    }
}
