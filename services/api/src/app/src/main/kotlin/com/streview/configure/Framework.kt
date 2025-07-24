package com.streview.configure

import com.streview.application.services.EncounterDecryptionConfig
import com.streview.application.services.EncounterDecryptionService
import com.streview.application.services.ImageStorageConfig
import com.streview.application.services.ImageStorageService
import com.streview.application.usecases.relays.MarkRelayStatusUseCase
import com.streview.application.usecases.stores.TryStoreUseCase
import com.streview.application.usecases.users.RegisterUserUseCase
import com.streview.domain.images.ImageRepository
import com.streview.domain.relays.RelayRepository
import com.streview.domain.stores.StoreRepository
import com.streview.domain.users.UserRepository
import com.streview.infrastructure.database.images.ImageRepositoryImpl
import com.streview.infrastructure.database.relays.RelayRepositoryImpl
import com.streview.infrastructure.database.stores.StoreRepositoryImpl
import com.streview.infrastructure.database.users.UserRepositoryImpl
import com.streview.infrastructure.storages.images.ImageStorageServiceImpl
import com.streview.service.relays.RelayDomainService
import io.ktor.server.application.*
import kotlinx.io.buffered
import kotlinx.io.files.Path
import kotlinx.io.files.SystemFileSystem
import kotlinx.io.readString
import org.koin.dsl.module
import org.koin.ktor.plugin.Koin

// トランザクションを簡単に貼りたいがさっぱりわからない
val useCaseModule = module {
    single<RegisterUserUseCase> {
        RegisterUserUseCase(get(), get(), get())
    }
    single<MarkRelayStatusUseCase> {
        MarkRelayStatusUseCase(get(), get())
    }
}

// アプリケーション層のサービスを依存関係に登録
val applicationServiceModule = module {
    single<ImageStorageService> {
        ImageStorageServiceImpl(get())
    }
    single<EncounterDecryptionService> {
        EncounterDecryptionService(get())
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
    single<UserRepository> {
        UserRepositoryImpl()
    }
    single<ImageRepository> {
        ImageRepositoryImpl()
    }
    single<StoreRepository> {
        StoreRepositoryImpl()
    }
    single<RelayRepository> {
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
    single<EncounterDecryptionConfig> {
        // 環境変数から秘密鍵ファイルのパスを取得し、読み込んだ内容をconfigに渡す
        val path = get<Application>().environment.config.config("app.security.secret").toString()
        EncounterDecryptionConfig(
            SystemFileSystem.source(Path(path)).buffered().readString()
        )
    }
}

fun Application.configureFramework() {
    install(Koin) {
        modules(
            module { single { this@configureFramework } },
            useCaseModule,
            applicationServiceModule,
            domainServiceModule,
            repositoryModule,
            configureModule
        )
    }
}
