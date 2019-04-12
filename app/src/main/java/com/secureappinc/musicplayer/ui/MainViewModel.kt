package com.secureappinc.musicplayer.ui

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.secureappinc.musicplayer.models.VideoEmplacement
import com.secureappinc.musicplayer.models.enteties.MusicTrack

/**
 **********************************
 * Created by Abdelhadi on 4/4/19.
 **********************************
 */
class MainViewModel : ViewModel() {

    val playVideo = MutableLiveData<String>()

    val currentVideo = MutableLiveData<MusicTrack>()


    var lastVideoEmplacement: VideoEmplacement? = null

}