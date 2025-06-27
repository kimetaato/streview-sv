package com.streview.application.usecases.users

import com.streview.application.services.ImageStorageService
import com.streview.application.usecases.UseCase

import com.streview.domain.users.IUserRepository
import com.streview.usecase.users.dto.UpdateProfileRequest
import com.streview.usecase.users.dto.UpdateProfileResponse

class UpdateUserProfile(private val repository: IUserRepository, private val imageStorageService: ImageStorageService) :
    UseCase<UpdateProfileRequest, UpdateProfileResponse> {
    override suspend fun execute(input: UpdateProfileRequest): UpdateProfileResponse {
        TODO("Not yet implemented")
    }
}