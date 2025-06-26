package com.streview.configure

import com.streview.domain.stores.Store
import com.streview.domain.stores.StoreRepository
import com.streview.domain.users.IUserRepository
import com.streview.infrastructure.database.stores.StoreRepositoryImpl
import com.streview.infrastructure.database.users.UserRepositoryImpl
import com.streview.usecase.stores.TryStoreUseCase
import com.streview.usecase.users.GetUserUseCase
import io.ktor.server.application.*
import org.koin.dsl.module
import org.koin.ktor.plugin.Koin


//  TODO: トランザクションを簡単に貼りたいがさっぱりわからない
val useCaseModule = module {
    single<GetUserUseCase> {
        GetUserUseCase(get())
    }
    single<TryStoreUseCase>{
        TryStoreUseCase(get())
    }
}

val repositoryModule = module {
    single<IUserRepository> {
        UserRepositoryImpl()
    }
    single<StoreRepository> {
        StoreRepositoryImpl()
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
