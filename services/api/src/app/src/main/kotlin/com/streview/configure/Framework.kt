package com.streview.configure

import com.streview.application.services.ImageStorageConfig
import com.streview.application.services.ImageStorageService
import com.streview.application.usecases.relays.MarkReviewAsRereviewedUseCase
import com.streview.application.usecases.users.RegisterUserUseCase
import com.streview.domain.images.ImageRepository
import com.streview.domain.relays.RelaysRepository
import com.streview.domain.stores.StoreRepository
import com.streview.domain.users.IUserRepository
import com.streview.infrastructure.database.images.ImageRepositoryImpl
import com.streview.infrastructure.database.relays.RelayRepositoryImpl
import com.streview.infrastructure.database.stores.StoreRepositoryImpl
import com.streview.infrastructure.database.users.UserRepositoryImpl
import com.streview.infrastructure.storages.images.ImageStorageServiceImpl
import com.streview.service.relays.RelayDomainService
import com.streview.usecase.stores.TryStoreUseCase
import io.ktor.server.application.*
import org.koin.dsl.module
import org.koin.ktor.plugin.Koin


//  TODO: トランザクションを簡単に貼りたいがさっぱりわからない
// アプリケーション層のユースケースを依存関係に登録
val useCaseModule = module {
    single<RegisterUserUseCase> {
        RegisterUserUseCase(get(), get(), get())
    }
    single<MarkReviewAsRereviewedUseCase> {
        MarkReviewAsRereviewedUseCase(get(), get())
    }
}

// アプリケーション層のサービスを依存関係に登録
val applicationServiceModule = module {
    single<ImageStorageService> {
        ImageStorageServiceImpl(get())
    }
    single<TryStoreUseCase> {
        TryStoreUseCase(get())
    }

}

// ドメイン層のサービスを依存関係に登録
val domainServiceModule = module {
    single<RelayDomainService> {
        RelayDomainService()
    }
}

// インフラストラクチャ層のリポジトリの実装を依存関係に登録
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
    single<RelaysRepository> {
        RelayRepositoryImpl()
    }
}

// 各種ファイルから読み取った値を依存関係に登録
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
            applicationServiceModule,
            repositoryModule,
            configureModule
        )
    }
}
