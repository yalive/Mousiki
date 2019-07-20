package com.cas.musicplayer.utils

import androidx.lifecycle.MutableLiveData
import com.cas.musicplayer.player.VideoEmplacement

/**
 **********************************
 * Created by Abdelhadi on 4/11/19.
 **********************************
 */

object VideoEmplacementLiveData : MutableLiveData<VideoEmplacement>() {

    var oldValue1: VideoEmplacement? = null
        private set

    var oldValue2: VideoEmplacement? = null
        private set

    fun center() {
        oldValue2 = oldValue1
        oldValue1 = value
        value = VideoEmplacement.center()
    }

    fun bottom() {
        oldValue2 = oldValue1
        oldValue1 = value
        value = VideoEmplacement.bottom()
    }

    fun playlist() {
        oldValue2 = oldValue1
        oldValue1 = value
        value = VideoEmplacement.playlist()
    }


    fun fullscreen() {
        oldValue2 = oldValue1
        oldValue1 = value
        value = VideoEmplacement.fullscreen()
    }

    fun out() {
        oldValue2 = oldValue1
        oldValue1 = value
        value = VideoEmplacement.out()
    }
}