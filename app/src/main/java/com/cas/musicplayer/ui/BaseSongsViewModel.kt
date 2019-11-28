package com.cas.musicplayer.ui

import com.cas.common.viewmodel.BaseViewModel
import com.cas.musicplayer.domain.model.MusicTrack
import com.cas.musicplayer.domain.usecase.recent.AddTrackToRecentlyPlayedUseCase
import com.cas.musicplayer.utils.uiCoroutine

/**
 ***************************************
 * Created by Abdelhadi on 2019-11-26.
 ***************************************
 */
open class BaseSongsViewModel constructor(
    private val addTrackToRecentlyPlayed: AddTrackToRecentlyPlayedUseCase
) : BaseViewModel() {

    fun saveTrackToRecent(track: MusicTrack) = uiCoroutine {
        addTrackToRecentlyPlayed(track)
    }
}