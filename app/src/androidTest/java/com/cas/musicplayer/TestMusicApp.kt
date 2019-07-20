package com.cas.musicplayer

import android.app.Application
import com.cas.musicplayer.di.ComponentProvider
import com.cas.musicplayer.di.DaggerTestAppComponent
import com.cas.musicplayer.di.TestAppComponent

/**
 ***************************************
 * Created by Abdelhadi on 2019-06-19.
 ***************************************
 */
class TestMusicApp : Application(), ComponentProvider {
    override val component: TestAppComponent by lazy {
        DaggerTestAppComponent.factory().create(this)
    }

    override fun onCreate() {
        super.onCreate()
    }
}