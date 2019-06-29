package com.secureappinc.musicplayer

import android.app.Application
import com.secureappinc.musicplayer.di.ComponentProvider
import com.secureappinc.musicplayer.di.DaggerTestAppComponent
import com.secureappinc.musicplayer.di.TestAppComponent

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