package com.streview.application.usecases

interface InputPort
interface OutputPort

interface UseCase<in I, out O> {
    suspend fun execute(input: I): O
}