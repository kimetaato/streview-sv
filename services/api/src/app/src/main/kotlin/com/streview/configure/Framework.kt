package com.streview.configure

import com.streview.domain.users.IUserRepository
import com.streview.infrastructure.database.users.UserRepositoryImpl
import com.streview.usecase.users.GetUserUseCase
import io.ktor.server.application.*
import org.koin.dsl.module
import org.koin.ktor.plugin.Koin


//  TODO: トランザクションを簡単に貼りたいがさっぱりわからない
val useCaseModule = module {
    single<GetUserUseCase> {
        GetUserUseCase(get())
    }
}

val repositoryModule = module {
    single<IUserRepository> {
        UserRepositoryImpl()
    }
}


fun Application.configureFramework() {
    install(Koin) {
        modules(
            useCaseModule,
            repositoryModule
        )
    }
}
