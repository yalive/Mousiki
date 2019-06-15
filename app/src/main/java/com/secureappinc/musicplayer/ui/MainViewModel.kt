package com.secureappinc.musicplayer.ui

import androidx.lifecycle.ViewModel
import com.secureappinc.musicplayer.player.VideoEmplacement
import javax.inject.Inject

/**
 **********************************
 * Created by Abdelhadi on 4/4/19.
 **********************************
 */
class MainViewModel @Inject constructor() : ViewModel() {

    var lastVideoEmplacement: VideoEmplacement? = null

}