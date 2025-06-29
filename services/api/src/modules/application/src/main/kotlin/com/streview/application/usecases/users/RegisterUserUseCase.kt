package com.streview.application.usecases.users

import com.streview.application.services.ImageStorageService
import com.streview.application.services.ImageType
import com.streview.application.usecases.UseCase
import com.streview.domain.commons.UserID
import com.streview.domain.exceptions.ConflictException
import com.streview.domain.images.Image
import com.streview.domain.images.ImageRepository
import com.streview.domain.users.IUserRepository
import com.streview.domain.users.Profile
import com.streview.domain.users.User
import com.streview.usecase.users.dto.RegisterUserRequest
import com.streview.usecase.users.dto.RegisterUserResponse
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import org.jetbrains.exposed.v1.r2dbc.transactions.suspendTransaction
import kotlin.random.Random

class RegisterUserUseCase(val uR: IUserRepository, val iR: ImageRepository, val iS: ImageStorageService) :
    UseCase<RegisterUserRequest, RegisterUserResponse> {
    override suspend fun execute(input: RegisterUserRequest): RegisterUserResponse {
        val user = suspendTransaction {
            println(input)
            // ユーザーIDを生成する
            val userID = UserID(input.userID)

            // ユーザーが存在しないことを確かめる
            uR.findByID(userID)?.let {
                throw ConflictException("すでに登録されています。")
            }

            // 画像のファイルパスを生成
            val fileName = "${Clock.System.now().toEpochMilliseconds()}_${Random.nextInt(1000, 10000)}"

            // 画像を保存する
            iS.save(input.imageSource, fileName, ImageType.UserIcon)

            // 画像ドメイン保存した画像の情報を保存する
            val image: Image = Image.create(fileName)

            // 画像パスを登録する
            iR.save(image)

            // 誕生日を日付型に変換
            val birthday = LocalDate.parse(input.birtDay)

            // 入力された情報を元にプロフィールを作成する
            val profile = Profile.create(input.name, birthday, input.gender, image.imageUUID)

            // ユーザードメインを作成する
            val newUser = User.create(userID, profile)

            // ユーザーを登録する
            uR.create(newUser)

        }
        //　登録したユーザーのIDを返す
        return RegisterUserResponse(user.userID.value)
    }
}