package com.cas.musicplayer.utils

import com.cas.musicplayer.BuildConfig

/**
 ***************************************
 * Created by Y.Abdelhadi on 5/6/20.
 ***************************************
 */
class EnvConfig {

    fun isDev() = BuildConfig.FLAVOR.contains("dev")

    fun isProd() = BuildConfig.FLAVOR.contains("prod")
}