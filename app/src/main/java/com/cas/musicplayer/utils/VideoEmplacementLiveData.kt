package com.cas.musicplayer.utils

import androidx.lifecycle.MutableLiveData
import com.cas.musicplayer.player.EmplacementInApp
import com.cas.musicplayer.player.VideoEmplacement

/**
 **********************************
 * Created by Abdelhadi on 4/11/19.
 **********************************
 */

object VideoEmplacementLiveData : MutableLiveData<VideoEmplacement>() {

    fun out() {
        value = VideoEmplacement.out()
    }

    fun inApp() {
        value = EmplacementInApp()
    }
}