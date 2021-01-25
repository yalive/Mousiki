package com.cas.musicplayer.di.injector

import android.app.Service
import com.cas.musicplayer.MusicApp
import com.cas.musicplayer.di.AppComponent


val Service.injector: AppComponent
    get() = (application as MusicApp).component
