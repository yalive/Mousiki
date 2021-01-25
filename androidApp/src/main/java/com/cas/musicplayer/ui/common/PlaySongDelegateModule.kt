package com.cas.musicplayer.ui.common

import dagger.Binds
import dagger.Module
import javax.inject.Singleton

/**
 ***************************************
 * Created by Abdelhadi on 2019-12-08.
 ***************************************
 */
@Module
interface PlaySongDelegateModule {

    @Singleton
    @Binds
    fun providesPlaySongDelegate(delegate: PlaySongDelegateImpl): PlaySongDelegate
}