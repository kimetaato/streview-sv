package com.streview.configure.dependency.domain

import com.streview.domain.encounters.EncounterRepository
import com.streview.domain.images.ImageRepository
import com.streview.domain.relays.RelayRepository
import com.streview.domain.reviews.ReviewRepository
import com.streview.domain.stores.StoreRepository
import com.streview.domain.users.UserRepository
import com.streview.domain.visits.VisitRepository
import com.streview.infrastructure.api.stores.GooglePlacesDataSource
import com.streview.infrastructure.api.stores.HotPepperDataSource
import com.streview.infrastructure.database.encounters.EncounterRepositoryImpl
import com.streview.infrastructure.database.images.ImageRepositoryImpl
import com.streview.infrastructure.database.relays.RelayRepositoryImpl
import com.streview.infrastructure.database.reviews.ReviewRepositoryImpl
import com.streview.infrastructure.database.stores.StoreDatabaseDataSource
import com.streview.infrastructure.database.users.UserRepositoryImpl
import com.streview.infrastructure.database.visits.VisitRepositoryImpl
import com.streview.infrastructure.repository.stores.StoreRepositoryImpl
import org.koin.dsl.module

val encounterModule = module {
    single<EncounterRepository> {
        EncounterRepositoryImpl()
    }
}

val imageModule = module {
    single<ImageRepository> {
        ImageRepositoryImpl()
    }
}

val relayModule = module {
    single<RelayRepository> {
        RelayRepositoryImpl()
    }
}

val reviewModule = module {
    single<ReviewRepository> {
        ReviewRepositoryImpl()
    }
}

val storeModule = module {
    single<StoreRepository> {
        StoreRepositoryImpl(get(), get(), get())
    }
    single<HotPepperDataSource> {
        HotPepperDataSource(get())
    }
    single<GooglePlacesDataSource> {
        GooglePlacesDataSource(get())
    }
    single<StoreDatabaseDataSource> {
        StoreDatabaseDataSource()
    }
}

val userModule = module {
    single<UserRepository> {
        UserRepositoryImpl()
    }
}

val visitModule = module {
    single<VisitRepository> {
        VisitRepositoryImpl()
    }
}

val repositoryModule = module {
    includes(
        encounterModule,
        imageModule,
        relayModule,
        reviewModule,
        storeModule,
        userModule,
        visitModule,
    )
}
