package com.cas.musicplayer.ui.local.folders

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.cas.musicplayer.MusicApp
import com.cas.musicplayer.ui.local.repository.LocalSongsRepository
import com.cas.musicplayer.ui.local.songs.HeaderSongsActionsItem
import com.cas.musicplayer.utils.fixedPath
import com.mousiki.shared.domain.models.*
import com.mousiki.shared.player.PlaySongDelegate
import com.mousiki.shared.player.updateCurrentPlaying
import com.mousiki.shared.ui.base.BaseViewModel
import kotlinx.coroutines.launch
import java.io.File

class FolderDetailsViewModel(
    private val localSongsRepository: LocalSongsRepository,
    private val playSongDelegate: PlaySongDelegate
) : BaseViewModel(), PlaySongDelegate by playSongDelegate {

    private val _localSongs = MutableLiveData<List<DisplayableItem>>()
    val localSongs: LiveData<List<DisplayableItem>>
        get() = _localSongs

    fun loadSongsFromPath(path: String) = viewModelScope.launch {
        val songsItems = localSongsRepository.songs()
            .filter { File(it.path).fixedPath(MusicApp.get()) == path }
            .map { song -> LocalSong(song).toDisplayedVideoItem() }

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
        _localSongs.value = displayedItems
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