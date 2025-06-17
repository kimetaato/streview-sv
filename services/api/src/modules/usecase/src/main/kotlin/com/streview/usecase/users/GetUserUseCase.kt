package com.streview.usecase.users

import com.streview.domain.exceptions.NotFoundException
import com.streview.domain.users.IUserRepository
import com.streview.domain.users.User
import com.streview.usecase.UseCase
import com.streview.usecase.users.dto.UserRequest
import com.streview.usecase.users.dto.UserResponse


class GetUserUseCase(private val repository: IUserRepository) : UseCase<UserRequest, UserResponse> {
    override suspend fun execute(input: UserRequest): UserResponse {
        println("Usecase GET / called")
        val user: User? = repository.findById(input.id)

        if (user != null) {
            return UserResponse(
                name = user.name,
                id = user.id,
                email = user.email
            )
        } else {
            throw NotFoundException("user is not found")
        }
    }
}