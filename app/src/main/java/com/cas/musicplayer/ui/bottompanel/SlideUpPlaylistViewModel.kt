package com.cas.musicplayer.ui.bottompanel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.cas.musicplayer.domain.model.MusicTrack
import com.cas.musicplayer.domain.usecase.recent.AddTrackToRecentlyPlayedUseCase
import com.cas.musicplayer.player.PlayerQueue
import com.cas.musicplayer.ui.BaseSongsViewModel
import com.cas.musicplayer.ui.home.model.DisplayedVideoItem
import com.cas.musicplayer.ui.home.model.toDisplayedVideoItem
import com.cas.musicplayer.utils.uiCoroutine
import javax.inject.Inject

/**
 ***************************************
 * Created by Abdelhadi on 2019-12-07.
 ***************************************
 */
class SlideUpPlaylistViewModel @Inject constructor(
    addTrackToRecentlyPlayed: AddTrackToRecentlyPlayedUseCase
) : BaseSongsViewModel(addTrackToRecentlyPlayed) {

    private val _playList = MediatorLiveData<List<DisplayedVideoItem>>()
    val playList: LiveData<List<DisplayedVideoItem>> = _playList

    init {
        _playList.addSource(PlayerQueue) {
            val tracks = PlayerQueue.queue ?: listOf()
            _playList.postValue(tracks.map { it.toDisplayedVideoItem() })
        }
    }

    fun onClickTrack(track: MusicTrack) = uiCoroutine {
        val tracks = PlayerQueue.queue ?: listOf()
        playTrackFromQueue(track, tracks)
    }
}