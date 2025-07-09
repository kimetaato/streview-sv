package com.streview.configure

import com.streview.application.events.EncounterAddEventHandler
import com.streview.application.services.EncounterDecryptionService
import com.streview.application.services.ImageStorageConfig
import com.streview.application.services.ImageStorageService
import com.streview.application.usecases.encounters.EncounterUseCase
import com.streview.application.usecases.users.RegisterUserUseCase
import com.streview.domain.commons.event.EventBus
import com.streview.domain.encounters.EncounterAddDomainEvent
import com.streview.domain.encounters.EncounterRepository
import com.streview.domain.images.ImageRepository
import com.streview.domain.stores.StoreRepository
import com.streview.domain.users.IUserRepository
import com.streview.infrastructure.database.encounters.EncounterRepositoryImpl
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
    single<EncounterUseCase> {
        EncounterUseCase(get(), get())
    }
}

val serviceModule = module {
    single<ImageStorageService> {
        ImageStorageServiceImpl(get())
    }
    single<TryStoreUseCase> {
        TryStoreUseCase(get())
    }
    single<EncounterDecryptionService> {
        // TODO: 設定から秘密鍵を取得する必要がある
        EncounterDecryptionService("test-key-32-characters-long-12")
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
    single<EncounterRepository> {
        EncounterRepositoryImpl()
    }
}

val eventModule = module {
    single {
        EventBus
    }
    single {
        EncounterAddEventHandler(
            // 依存関係があれば取得
        )
    }

    // イベントを購読する
    factory { (eventBus: EventBus) ->
        {
            // EventHandlerを登録
            eventBus.subscribe(EncounterAddDomainEvent::class.java, get<EncounterAddEventHandler>())
        }
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
            configureModule,
            eventModule
        )
    }
}

