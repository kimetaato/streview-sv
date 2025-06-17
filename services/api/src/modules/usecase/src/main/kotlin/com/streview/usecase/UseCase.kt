package com.streview.usecase

interface InputPort
interface OutputPort

interface UseCase<in I : InputPort, out O : OutputPort> {
    suspend fun execute(input: I): O
}