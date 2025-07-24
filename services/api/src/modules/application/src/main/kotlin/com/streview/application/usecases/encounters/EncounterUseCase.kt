package com.streview.application.usecases.encounters

import com.streview.application.services.EncounterDecryptionService
import com.streview.application.usecases.UseCase
import com.streview.application.usecases.encounters.dto.EncounterRequest
import com.streview.application.usecases.encounters.dto.EncounterResponse
import com.streview.domain.commons.event.EventBus
import com.streview.domain.encounters.EncounterRepository
import com.streview.domain.exceptions.DuplicateEncounterException

class EncounterUseCase(
    val eR: EncounterRepository,
    private val dS: EncounterDecryptionService
) : UseCase<EncounterRequest, EncounterResponse> {
    override suspend fun execute(input: EncounterRequest): EncounterResponse {
        var encounterCount = 0
        // 日付ごとのエンティティで処理を行う
        input.encounters.map {
            val encounter = eR.findByID(input.userID, it.date)

            // すれ違いを追加
            it.encounter.forEach { encryptedEncounterID ->
                // 暗号化されたencounterIDから実際のUserIDを抽出
                val actualUserID = dS.extractUserID(encryptedEncounterID)
                try {
                    encounter.add(actualUserID)
                    encounterCount++
                } catch (_: DuplicateEncounterException) {}
            }

            // 永続化
            eR.save(encounter)

            // イベント発行
            encounter.domainEvents.map { event ->
                EventBus.publish(event)
            }
        }

        // レビューの受信があればtrueを返す
        return EncounterResponse(encounterCount)
    }
}
