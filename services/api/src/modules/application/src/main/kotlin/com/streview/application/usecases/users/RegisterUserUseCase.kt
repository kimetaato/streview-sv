package com.streview.application.usecases.users

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.andThen
import com.github.michaelbull.result.fold
import com.streview.application.services.ImageStorageService
import com.streview.application.services.ImageType
import com.streview.application.usecases.UseCase
import com.streview.common.dto.users.RegisterUserRequest
import com.streview.common.dto.users.RegisterUserResponse
import com.streview.domain.commons.UserID
import com.streview.domain.commons.errors.EntityError
import com.streview.domain.images.Image
import com.streview.domain.images.ImageRepository
import com.streview.domain.users.Profile
import com.streview.domain.users.User
import com.streview.domain.users.UserRepository
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import org.jetbrains.exposed.v1.r2dbc.transactions.suspendTransaction
import kotlin.random.Random

class RegisterUserUseCase(
    private val userRepository: UserRepository,
    private val imageRepository: ImageRepository,
    private val imageStorageService: ImageStorageService
) :
    UseCase<RegisterUserRequest, RegisterUserResponse> {
    override suspend fun execute(input: RegisterUserRequest): RegisterUserResponse =
        suspendTransaction {
            // ユーザーIDを生成する
            val userID = UserID(input.userID)
            val birthday = LocalDate.parse(input.birtDay)

            // ユーザーが存在しないことを確かめる
            userRepository.findByID(userID)
                .andThen { user ->
                    user?.let {
                        Err(EntityError.AlreadyExist)
                    } ?: Ok(Unit)
                }.andThen {
                    // 画像のファイルパスを生成
                    val fileName = "${Clock.System.now().toEpochMilliseconds()}_${Random.nextInt(1000, 10000)}"

                    // 画像を保存する
                    imageStorageService.save(input.imageSource, fileName, ImageType.UserIcon)

                    // 画像ドメイン保存した画像の情報を保存する
                    val image: Image = Image.create(fileName)

                    // 画像パスを登録する
                    imageRepository.save(image)
                }.andThen { image ->
                    // 入力された情報を元にプロフィールを作成する
                    val profile = Profile.create(input.name, birthday, input.gender, image.imageUUID)
                    // ユーザードメインを作成する
                    val newUser = User.create(userID, profile)

                    userRepository.save(newUser)
                }
        }.fold(
            success = { user ->
                RegisterUserResponse(user.userID.value)
            },
            failure = { domainError ->
                throw domainError
            }
        )
}
