package com.streview.configure

import com.streview.application.services.EncounterDecryptionConfig
import com.streview.application.services.ImageStorageConfig
import com.streview.configure.dependency.application.applicationServiceModule
import com.streview.configure.dependency.application.useCaseModule
import com.streview.configure.dependency.domain.domainServiceModule
import com.streview.configure.dependency.domain.eventModule
import com.streview.configure.dependency.domain.repositoryModule
import io.ktor.client.HttpClient
import io.ktor.server.application.*
import kotlinx.io.buffered
import kotlinx.io.files.Path
import kotlinx.io.files.SystemFileSystem
import kotlinx.io.readString
import org.koin.dsl.module
import org.koin.ktor.plugin.Koin

// HTTPクライアント設定
val httpClientModule = module {
    single<HttpClient> {
        createHttpClient()
    }
}

// 各種ファイルから読み取った値を依存関係に登録
val configureModule = module {
    single<ImageStorageConfig> {
        ImageStorageConfig()
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
            httpClientModule,
            configureModule,
            eventModule
        )
    }
}
