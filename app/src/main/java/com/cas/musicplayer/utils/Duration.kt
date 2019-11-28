package com.cas.musicplayer.utils

import kotlin.time.Duration
import kotlin.time.ExperimentalTime
import kotlin.time.milliseconds

/**
 ***************************************
 * Created by Abdelhadi on 2019-11-24.
 ***************************************
 */
@ExperimentalTime
val Duration.fromNow
    get(): Duration {
        val currentDuration = System.currentTimeMillis().milliseconds
        return this + currentDuration
    }