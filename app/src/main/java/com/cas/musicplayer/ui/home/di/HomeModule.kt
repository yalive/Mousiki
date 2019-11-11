package com.cas.musicplayer.ui.home.di

import com.cas.musicplayer.ui.home.data.repository.HomeRepositoryImpl
import com.cas.musicplayer.ui.home.domain.repository.HomeRepository
import dagger.Binds
import dagger.Module

/**
 ***************************************
 * Created by Abdelhadi on 2019-11-11.
 ***************************************
 */
@Module
internal abstract class HomeModule {
    @Binds
    abstract fun bindRepository(repository: HomeRepositoryImpl): HomeRepository
}
