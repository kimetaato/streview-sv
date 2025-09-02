package com.streview.application.usecases.users

import com.streview.application.services.ImageStorageService
import com.streview.application.usecases.UseCase
import com.streview.common.dto.users.UpdateProfileRequest
import com.streview.common.dto.users.UpdateProfileResponse
import com.streview.domain.users.UserRepository

class UpdateUserProfile(private val repository: UserRepository, private val imageStorageService: ImageStorageService) :
    UseCase<UpdateProfileRequest, UpdateProfileResponse> {
    override suspend fun execute(input: UpdateProfileRequest): UpdateProfileResponse {
        TODO("Not yet implemented")
    }
}
