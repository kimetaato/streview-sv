package com.streview.configure.dependency.domain

import com.streview.domain.commons.event.EventBus
import org.koin.dsl.module

val eventModule = module {
    single {
        EventBus
    }
//    single {
//        EncounterAddEventHandler(get(), get())
//    }
//
//    // イベントを購読する
//    factory { (eventBus: EventBus) ->
//        {
//            // EventHandlerを登録
//            eventBus.subscribe(EncounterAddDomainEvent::class.java, get<EncounterAddEventHandler>())
//        }
//    }
}
