package com.cas.musicplayer.ui

import com.cas.common.viewmodel.BaseViewModel
import com.cas.musicplayer.domain.model.MusicTrack
import com.cas.musicplayer.player.VideoEmplacement
import com.cas.musicplayer.ui.common.PlaySongDelegate
import com.cas.musicplayer.utils.uiCoroutine
import javax.inject.Inject

/**
 **********************************
 * Created by Abdelhadi on 4/4/19.
 **********************************
 */
class MainViewModel @Inject constructor(
    delegate: PlaySongDelegate
) : BaseViewModel(), PlaySongDelegate by delegate {

    var lastVideoEmplacement: VideoEmplacement? = null

    fun playTrackFromDeepLink(track: MusicTrack) = uiCoroutine {
        playTrackFromQueue(track, listOf(track))
    }
}