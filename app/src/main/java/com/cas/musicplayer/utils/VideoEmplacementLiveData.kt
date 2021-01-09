package com.cas.musicplayer.utils

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.cas.musicplayer.player.EmplacementInApp
import com.cas.musicplayer.player.VideoEmplacement
import com.cas.musicplayer.ui.player.TAG_SERVICE

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