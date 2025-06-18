package com.streview.usecase.users.dto

import com.streview.usecase.InputPort
import com.streview.usecase.OutputPort
import kotlinx.serialization.Serializable

@Serializable
data class GetUserRequest(val id: Int) : InputPort

@Serializable
data class GetUserResponse(val id: Int, val name: String, val email: String) : OutputPort