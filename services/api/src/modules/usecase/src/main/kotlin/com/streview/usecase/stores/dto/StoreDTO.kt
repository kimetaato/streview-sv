package com.streview.usecase.stores.dto

import com.streview.usecase.InputPort
import com.streview.usecase.OutputPort
import kotlinx.serialization.Serializable


@Serializable
data class TryStoreRequest(val id: String): InputPort

@Serializable
data class TryStoreResponse(val factoryId: String, val reconstructName: String ): OutputPort