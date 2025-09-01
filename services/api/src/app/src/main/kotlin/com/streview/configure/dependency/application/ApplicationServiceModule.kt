package com.streview.configure.dependency.application

import com.streview.application.services.EncounterDecryptionService
import com.streview.application.services.ImageStorageService
import com.streview.infrastructure.storages.images.ImageStorageServiceImpl
import org.koin.dsl.module

val applicationServiceModule = module {
    single<ImageStorageService> {
        ImageStorageServiceImpl(get(), get())
    }
    single<EncounterDecryptionService> {
        EncounterDecryptionService(get())
    }
}
