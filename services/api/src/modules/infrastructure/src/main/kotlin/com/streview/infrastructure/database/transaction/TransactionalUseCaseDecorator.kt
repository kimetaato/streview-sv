package com.streview.infrastructure.database.transaction

import com.streview.usecase.InputPort
import com.streview.usecase.OutputPort
import com.streview.usecase.UseCase
import org.jetbrains.exposed.v1.r2dbc.transactions.suspendTransaction


class TransactionalUseCaseDecorator<in I : InputPort, out O : OutputPort>(val decorated: UseCase<I, O>) :
    UseCase<I, O> {
    override suspend fun execute(input: I): O {

        // ユースケースに対してトランザクションを張る
        val res = suspendTransaction {
            decorated.execute(input)
        }

        // 返す
        return res
    }
}