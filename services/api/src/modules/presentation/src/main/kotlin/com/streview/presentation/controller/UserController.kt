package com.streview.presentation.controller


import com.streview.application.usecases.users.RegisterUserUseCase
import com.streview.domain.exceptions.InvalidInputException
import com.streview.usecase.users.dto.RegisterUserRequest
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.utils.io.*
import kotlinx.io.Source
import org.koin.ktor.ext.inject

fun Route.userController() {
    val registerUserUseCase by inject<RegisterUserUseCase>()

    route("/users") {
        // ユーザーを登録する
        post("/registers") {
            // jwtを解析して得られたuserIDを取得
            val userID = call.principal<UserIdPrincipal>()!!.name

            // multipart/form-dataをして送信されたデータを取得する
            val multiplatform = call.receiveMultipart() // デフォルトで50MBまでの受信に対応している

            var name: String? = null
            var birthday: String? = null
            var gender: String? = null
            var imageSource: Source? = null

            multiplatform.forEachPart { part ->
                when (part) {
                    // ファイルとして送信されたものを受け取る
                    is PartData.FileItem -> imageSource = part.provider().readBuffer()
                    //
                    is PartData.FormItem -> when (part.name) {
                        "name" -> name = part.value
                        "birthday" -> birthday = part.value
                        "gender" -> gender = part.value
                    }
                    // BinaryItemなどを考慮
                    else -> Unit
                }
                part.dispose()
            }
            // それぞれの入力値がからでないことを確認する
            val res = if (name != null || birthday != null || gender != null || imageSource != null) {
                registerUserUseCase.execute(RegisterUserRequest(userID, name!!, birthday!!, gender!!, imageSource!!))
            } else {
                throw InvalidInputException("入力項目が不足しています。")
            }

            call.respond(HttpStatusCode.Created, res)
        }
        // ユーザーのプロフィールを取得する
        route("/profiles") {
            // ユーザーのプロフィールを取得する
            get {

            }
            // ユーザーのプロフィールを変更する
            put {

            }
        }

    }
}