package com.secureappinc.musicplayer.ui

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.secureappinc.musicplayer.models.SnippetVideo
import com.secureappinc.musicplayer.models.VideoEmplacement

/**
 **********************************
 * Created by Abdelhadi on 4/4/19.
 **********************************
 */
class MainViewModel : ViewModel() {

    val playVideo = MutableLiveData<String>()

    val currentVideo = MutableLiveData<SnippetVideo>()


    var lastVideoEmplacement: VideoEmplacement? = null
}