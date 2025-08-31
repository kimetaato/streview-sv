package com.streview.application.usecases.visits.dto

import com.streview.application.usecases.InputPort
import com.streview.application.usecases.OutputPort
import kotlinx.serialization.Serializable

data class CheckInRequest(
    val userID: String,
    val storeUUID: String,
) : InputPort

@Serializable
data class CheckInResponse(
    val storeUUID: String
) : OutputPort

data class VisitStatusRequest(
    val userID: String,
    val storeUUID: String,
    val status: String
) : InputPort

@Serializable
data class VisitStatusResponse(
    val storeUUID: String
) : OutputPort
