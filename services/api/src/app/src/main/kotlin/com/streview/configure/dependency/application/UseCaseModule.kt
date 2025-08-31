package com.streview.configure.dependency.application

import com.streview.application.usecases.encounters.EncounterUseCase
import com.streview.application.usecases.relays.MarkRelayStatusUseCase
import com.streview.application.usecases.reviews.GetMyReviewUseCase
import com.streview.application.usecases.reviews.PostReviewUseCase
import com.streview.application.usecases.stores.GetGeofenceUseCase
import com.streview.application.usecases.users.RegisterUserUseCase
import org.koin.dsl.module

val encounterModule = module {
    single<EncounterUseCase> {
        EncounterUseCase(get(), get())
    }
}

// val imageModule = module {
//
// }

val relayModule = module {
    single<MarkRelayStatusUseCase> {
        MarkRelayStatusUseCase(get(), get())
    }
}

val reviewModule = module {
    single<GetMyReviewUseCase> {
        GetMyReviewUseCase(get(), get(), get())
    }

    single<PostReviewUseCase> {
        PostReviewUseCase(get(), get(), get())
    }
}

val storeModule = module {
    single<GetGeofenceUseCase> {
        GetGeofenceUseCase(get())
    }
}

val userModule = module {
    single<RegisterUserUseCase> {
        RegisterUserUseCase(get(), get(), get())
    }
}

val useCaseModule = module {
    includes(
        encounterModule,
        relayModule,
        reviewModule,
        storeModule,
        userModule,
    )
}
