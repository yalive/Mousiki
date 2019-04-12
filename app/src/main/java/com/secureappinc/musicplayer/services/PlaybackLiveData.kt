package com.secureappinc.musicplayer.services

import androidx.lifecycle.MutableLiveData
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants

/**
 **********************************
 * Created by Abdelhadi on 4/10/19.
 **********************************
 */

data class VideoPlayBackState(val state: PlayerConstants.PlayerState, val fromUser: Boolean)

object PlaybackLiveData : MutableLiveData<VideoPlayBackState>()