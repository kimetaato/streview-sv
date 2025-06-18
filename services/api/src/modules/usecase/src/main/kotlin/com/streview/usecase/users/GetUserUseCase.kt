package com.streview.usecase.users

import com.streview.domain.exceptions.NotFoundException
import com.streview.domain.users.IUserRepository
import com.streview.domain.users.User
import com.streview.usecase.UseCase
import com.streview.usecase.users.dto.GetUserRequest
import com.streview.usecase.users.dto.GetUserResponse


class GetUserUseCase(private val repository: IUserRepository) : UseCase<GetUserRequest, GetUserResponse> {
    override suspend fun execute(input: GetUserRequest): GetUserResponse {
        println("Usecase GET / called")
        val user: User? = repository.findById(input.id)

       return user?.let {
            GetUserResponse(
                name = user.name.value,
                id = user.id.value,
                email = user.email.value,
            )
        } ?: throw NotFoundException("user is not found")
    }
}