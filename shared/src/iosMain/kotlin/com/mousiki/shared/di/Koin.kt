package com.mousiki.shared.di

import com.mousiki.shared.utils.StorageApi
import org.koin.dsl.bind
import org.koin.dsl.module

fun initIOSKoin(dependenciesProvider: IOSDependenciesProvider) {
    val iOSModule = module {
        single { dependenciesProvider.storage } bind StorageApi::class
    }
    initKoin(iOSModule)
}


interface IOSDependenciesProvider {
    val storage: StorageApi
}