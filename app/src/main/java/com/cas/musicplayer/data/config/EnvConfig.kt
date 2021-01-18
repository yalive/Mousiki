package com.cas.musicplayer.data.config

import com.cas.musicplayer.BuildConfig
import dagger.Reusable
import javax.inject.Inject

/**
 ***************************************
 * Created by Y.Abdelhadi on 5/6/20.
 ***************************************
 */
@Reusable
class EnvConfig @Inject constructor() {

    fun isDev() = BuildConfig.FLAVOR.contains("dev")

    fun isProd() = BuildConfig.FLAVOR.contains("prod")
}