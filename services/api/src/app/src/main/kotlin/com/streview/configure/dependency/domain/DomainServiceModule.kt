package com.streview.configure.dependency.domain

import com.streview.service.relays.RelayDomainService
import org.koin.dsl.module

val domainServiceModule = module {
    single<RelayDomainService> {
        RelayDomainService()
    }
}
