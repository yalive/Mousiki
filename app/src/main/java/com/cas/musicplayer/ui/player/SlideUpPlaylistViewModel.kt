package com.cas.musicplayer.ui.player

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.cas.common.viewmodel.BaseViewModel
import com.mousiki.shared.domain.models.MusicTrack
import com.cas.musicplayer.player.PlayerQueue
import com.cas.musicplayer.ui.common.PlaySongDelegate
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
    delegate: PlaySongDelegate
) : BaseViewModel(), PlaySongDelegate by delegate {

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