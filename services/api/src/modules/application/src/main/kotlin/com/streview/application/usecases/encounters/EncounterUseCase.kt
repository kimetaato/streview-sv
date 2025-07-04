package com.streview.application.usecases.encounters

import com.streview.application.services.EncounterDecryptionService
import com.streview.application.usecases.UseCase
import com.streview.application.usecases.encounters.dto.EncounterRequest
import com.streview.application.usecases.encounters.dto.EncounterResponse
import com.streview.domain.commons.UserID
import com.streview.domain.commons.event.EventBus
import com.streview.domain.encounters.EncounterDate
import com.streview.domain.encounters.EncounterRepository
import kotlinx.datetime.LocalDate


class EncounterUseCase(
    val eR: EncounterRepository,
    private val dS: EncounterDecryptionService
): UseCase<EncounterRequest, EncounterResponse> {
    override suspend fun execute(input: EncounterRequest): EncounterResponse {

        // ユーザーIDを生成する
        val userID = UserID(input.userID)

        input.encounters.map { item ->
            val encounterDate = EncounterDate(LocalDate.parse(item.key))
            val encounter = eR.findByID(userID,encounterDate)

            // すれ違いを追加
            item.value.forEach { encryptedEncounterID ->
                // 暗号化されたencounterIDから実際のUserIDを抽出
                val actualUserID = dS.extractUserID(encryptedEncounterID)
                encounter.add(actualUserID)
            }

            // 永続化
            eR.save(encounter)

            // イベント発行
            encounter.domainEvents.map {
                EventBus.publish(it)
            }
        }

        // レビューの受信があればtrueを返す
        return EncounterResponse(input.encounters.isNotEmpty())
    }
}