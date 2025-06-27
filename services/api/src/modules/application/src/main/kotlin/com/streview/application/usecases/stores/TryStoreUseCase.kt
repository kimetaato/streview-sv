package com.streview.usecase.stores

import com.streview.application.usecases.UseCase
import com.streview.domain.stores.Store
import com.streview.domain.stores.StoreRepository
import com.streview.usecase.stores.dto.TryStoreRequest
import com.streview.usecase.stores.dto.TryStoreResponse

class TryStoreUseCase(private val repository: StoreRepository) : UseCase<TryStoreRequest, TryStoreResponse> {
    override suspend fun execute(input: TryStoreRequest): TryStoreResponse {
        println("UseCase GET / called") // TODO: called?

        val factoryStore: Store? = Store.factory("ほげら")
        val reconstructStore: Store? = repository.findById(input.id) // "uuid"

        return TryStoreResponse(
            factoryId = factoryStore?.id?.value ?: "",
            reconstructName = reconstructStore?.name?.value ?: "",
        )
    }
}