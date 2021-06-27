package com.cas.musicplayer.ui.local.songs

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.cas.musicplayer.ui.local.repository.LocalSongsRepository
import com.mousiki.shared.domain.models.*
import com.mousiki.shared.player.PlaySongDelegate
import com.mousiki.shared.player.updateCurrentPlaying
import com.mousiki.shared.ui.base.BaseViewModel
import kotlinx.coroutines.launch

class LocalSongsViewModel(
    private val localSongsRepository: LocalSongsRepository,
    private val playSongsDelegate: PlaySongDelegate,
) : BaseViewModel(), PlaySongDelegate by playSongsDelegate {

    private val _localSongs = MutableLiveData<List<DisplayableItem>>()
    val localSongs: LiveData<List<DisplayableItem>>
        get() = _localSongs

    init {
        loadAllSongs()
    }

    fun onClickTrack(track: Track) = scope.launch {
        val tracks = _localSongs.value
            ?.filterIsInstance<DisplayedVideoItem>()
            ?.map { it.track } ?: return@launch
        playTrackFromQueue(track, tracks)
    }

    fun onPlaybackStateChanged() {
        val currentItems = _localSongs.value ?: return
        val updatedList = updateCurrentPlaying(currentItems)
        _localSongs.value = updatedList
    }

    private fun loadAllSongs() {
        _localSongs.value = localSongsRepository.songs().map {
            LocalSong(it).toDisplayedVideoItem()
        }
    }
}