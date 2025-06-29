package com.streview.configure

import com.streview.application.services.ImageStorageConfig
import com.streview.application.services.ImageStorageService
import com.streview.application.usecases.users.RegisterUserUseCase
import com.streview.domain.images.ImageRepository
import com.streview.domain.stores.StoreRepository
import com.streview.domain.users.IUserRepository
import com.streview.infrastructure.database.images.ImageRepositoryImpl
import com.streview.infrastructure.database.stores.StoreRepositoryImpl
import com.streview.infrastructure.database.users.UserRepositoryImpl
import com.streview.infrastructure.storages.images.ImageStorageServiceImpl
import com.streview.usecase.stores.TryStoreUseCase
import io.ktor.server.application.*
import org.koin.dsl.module
import org.koin.ktor.plugin.Koin


//  TODO: トランザクションを簡単に貼りたいがさっぱりわからない
val useCaseModule = module {
    single<RegisterUserUseCase> {
        RegisterUserUseCase(get(), get(), get())
    }
}

val serviceModule = module {
    single<ImageStorageService> {
        ImageStorageServiceImpl(get())
    }
    single<TryStoreUseCase> {
        TryStoreUseCase(get())
    }
}

val repositoryModule = module {
    single<IUserRepository> {
        UserRepositoryImpl()
    }
    single<ImageRepository> {
        ImageRepositoryImpl()
    }
    single<StoreRepository> {
        StoreRepositoryImpl()
    }
}

val configureModule = module {
    single<ImageStorageConfig> {
        val imageConfigSection = get<Application>().environment.config.config("app.storage.images")
        ImageStorageConfig(
            imageConfigSection.keys().associateWith { key ->
                imageConfigSection.property(key).getString()
            }
        )
    }
}


fun Application.configureFramework() {
    install(Koin) {
        modules(
            module { single { this@configureFramework } },
            useCaseModule,
            serviceModule,
            repositoryModule,
            configureModule
        )
    }
}
