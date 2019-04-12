package com.secureappinc.musicplayer.utils

import androidx.lifecycle.MutableLiveData
import com.secureappinc.musicplayer.models.VideoEmplacement

/**
 **********************************
 * Created by Abdelhadi on 4/11/19.
 **********************************
 */

object VideoEmplacementLiveData : MutableLiveData<VideoEmplacement>() {

    fun center() {
        value = VideoEmplacement.center()
    }

    fun bottom() {
        value = VideoEmplacement.bottom()
    }

    fun playlist() {
        value = VideoEmplacement.playlist()
    }

    fun out() {
        value = VideoEmplacement.out()
    }
}