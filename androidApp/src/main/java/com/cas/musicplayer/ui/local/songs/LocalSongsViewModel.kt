package com.cas.musicplayer.ui.local.songs

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
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

    fun loadAllSongs() = viewModelScope.launch {
        val songsItems = localSongsRepository.songs().map {
            LocalSong(it).toDisplayedVideoItem()
        }
        val displayedItems = mutableListOf<DisplayableItem>().apply {
            add(HeaderSongsActionsItem(songsItems.size,
                onPlayAllTracks = {
                    if (songsItems.isEmpty()) return@HeaderSongsActionsItem
                    onClickTrack(songsItems[0].track)
                },
                onShuffleAllTracks = { onShufflePlay() }
            ))
            addAll(songsItems)
        }
        _localSongs.value = updateCurrentPlaying(displayedItems)
    }

    fun onClickTrack(track: Track) = scope.launch {
        val tracks = _localSongs.value
            ?.filterIsInstance<DisplayedVideoItem>()
            ?.map { it.track } ?: return@launch
        playTrackFromQueue(track, tracks)
    }

    private fun onShufflePlay() = scope.launch {
        val tracks = _localSongs.value
            ?.filterIsInstance<DisplayedVideoItem>()
            ?.map { it.track }?.shuffled() ?: return@launch

        if (tracks.isEmpty()) return@launch
        playTrackFromQueue(tracks.random(), tracks)
    }

    fun onPlaybackStateChanged() {
        val currentItems = _localSongs.value ?: return
        val updatedList = updateCurrentPlaying(currentItems)
        _localSongs.value = updatedList
    }
}