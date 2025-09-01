package com.streview.domain.visits

import com.streview.domain.commons.UUID
import com.streview.domain.commons.UserID
import com.streview.domain.visits.vo.Status

class Visit private constructor(
    val userID: UserID,
    val storeUUID: UUID,
    private var _visitCount: Int,
    private var _status: Status
) {
    val visitCount: Int
        get() = _visitCount
    val status: Status
        get() = _status

    companion object {
        fun create(
            userID: UserID,
            storeUUID: UUID,
        ): Visit {
            return Visit(
                userID = userID,
                storeUUID = storeUUID,
                _visitCount = 0,
                _status = Status.Neutral,
            )
        }
        fun reconstruct(
            userID: String,
            storeUUID: String,
            visitCount: Int,
            status: String
        ): Visit {
            return Visit(
                userID = UserID(userID),
                storeUUID = UUID(storeUUID),
                _visitCount = visitCount,
                _status = Status.entries.first { it.value == status }
            )
        }
    }

    fun incrementVisitCount() {
        _visitCount++
    }

    fun setStatus(status: Status) {
        _status = status
    }
}
