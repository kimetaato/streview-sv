package com.streview.application.usecases

import com.streview.common.dto.InputPort
import com.streview.common.dto.OutputPort

interface UseCase<in I : InputPort, out O : OutputPort> {
    suspend fun execute(input: I): O
}
