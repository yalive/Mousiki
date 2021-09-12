package com.cas.musicplayer.ui.local.videos.history

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.cas.musicplayer.ui.local.repository.LocalVideosRepository
import com.cas.musicplayer.ui.local.repository.filterNotHidden
import com.mousiki.shared.domain.models.*
import com.mousiki.shared.player.PlaySongDelegate
import com.mousiki.shared.player.updateCurrentPlaying
import com.mousiki.shared.ui.base.BaseViewModel
import com.mousiki.shared.ui.resource.Resource
import kotlinx.coroutines.*

class PlayedVideoViewModel(
    private val localSongsRepository: LocalVideosRepository,
    private val playSongsDelegate: PlaySongDelegate,
) : BaseViewModel(), PlaySongDelegate by playSongsDelegate {

    private val _playedVideos = MutableLiveData<Resource<List<DisplayableItem>>>()
    val playedVideos: LiveData<Resource<List<DisplayableItem>>>
        get() = _playedVideos

    init {
        getAllPlayedVideos()
    }

    fun getAllPlayedVideos() = viewModelScope.launch {
        _playedVideos.value = Resource.Loading
        val videos = localSongsRepository.videos().filterNotHidden()
        val videosItems = videos.map {
            LocalSong(it).toDisplayedVideoItem()
        }

        _playedVideos.value = Resource.Success(updateCurrentPlaying(videosItems))

    }

   /* fun onClickTrack(track: Track) = scope.launch {
        val tracks = _playedVideos
            ?.filterIsInstance<DisplayedVideoItem>()
            ?.map { it.track } ?: return@launch
        playTrackFromQueue(track, tracks)
    }

    fun onPlaybackStateChanged() {
        val currentItems = _playedVideos.value ?: return
        val updatedList = updateCurrentPlaying(currentItems)
        _playedVideos.value = updatedList
    }*/
}