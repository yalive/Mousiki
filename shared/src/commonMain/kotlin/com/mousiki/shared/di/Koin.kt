package com.mousiki.shared.di

import org.koin.core.context.startKoin
import org.koin.core.module.Module

fun initKoin(vararg appModules: Module) {
    startKoin {
        modules(
            *appModules,
            platformModule,
            networkModule,
            repositoriesModule,
            dataSourcesModule,
            mappersModule,
            useCasesModule,
        )
    }
}