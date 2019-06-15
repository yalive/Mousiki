package com.secureappinc.musicplayer.utils.Extensions

import android.app.Activity
import com.secureappinc.musicplayer.di.ComponentProvider

/**
 ***************************************
 * Created by Abdelhadi on 2019-06-14.
 ***************************************
 */
val Activity.injector
    get() = (application as ComponentProvider).component