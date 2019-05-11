package com.secureappinc.musicplayer.utils

import androidx.lifecycle.MutableLiveData
import com.secureappinc.musicplayer.MusicApp
import com.secureappinc.musicplayer.dpToPixel

/**
 **********************************
 * Created by Abdelhadi on 2019-05-11.
 **********************************
 */
object DeviceInset : MutableLiveData<ScreenInset>() {

    fun get(): ScreenInset {
        if (value != null) {
            return value!!
        }

        return ScreenInset(0, dpToPixel(24f, MusicApp.get()).toInt(), 0, 0)
    }

    fun hasNotch(): Boolean {
        val regularStatusBarHeight = dpToPixel(24f, MusicApp.get())
        val paddingTop = get().top

        if (paddingTop > regularStatusBarHeight + 10) { // 10 for approximation
            // Has notch
            return true
        }

        return false
    }
}

data class ScreenInset(val left: Int, val top: Int, val right: Int, val bottom: Int)