package com.cas.musicplayer.player.services

import androidx.lifecycle.MutableLiveData
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants

/**
 **********************************
 * Created by Abdelhadi on 4/10/19.
 **********************************
 */

object PlaybackLiveData : MutableLiveData<PlayerConstants.PlayerState>()

object PlaybackDuration : MutableLiveData<Float>()