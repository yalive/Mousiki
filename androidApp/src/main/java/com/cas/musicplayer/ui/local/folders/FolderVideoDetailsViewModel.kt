package com.cas.musicplayer.ui.local.folders

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.cas.musicplayer.MusicApp
import com.cas.musicplayer.ui.local.repository.LocalVideosRepository
import com.cas.musicplayer.ui.local.songs.HeaderSongsActionsItem
import com.cas.musicplayer.ui.local.videos.HeaderVideosActionsItem
import com.cas.musicplayer.utils.fixedPath
import com.mousiki.shared.domain.models.*
import com.mousiki.shared.player.PlaySongDelegate
import com.mousiki.shared.player.updateCurrentPlaying
import com.mousiki.shared.ui.base.BaseViewModel
import kotlinx.coroutines.launch
import java.io.File

class FolderVideoDetailsViewModel(
    private val localVideosRepository: LocalVideosRepository,
    private val playSongDelegate: PlaySongDelegate
) : BaseViewModel(), PlaySongDelegate by playSongDelegate {

    private val _localSongs = MutableLiveData<List<DisplayableItem>>()
    val localSongs: LiveData<List<DisplayableItem>>
        get() = _localSongs

    fun loadSongsFromPath(path: String) = viewModelScope.launch {
        val songsItems = localVideosRepository.videos()
            .filter { File(it.path).fixedPath(MusicApp.get()) == path }
            .map { song -> LocalSong(song).toDisplayedVideoItem() }

        val displayedItems = mutableListOf<DisplayableItem>().apply {
            add(HeaderVideosActionsItem(songsItems.size,
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

    fun onPlaybackStateChanged() {
        val currentItems = _localSongs.value ?: return
        val updatedList = updateCurrentPlaying(currentItems)
        _localSongs.value = updatedList
    }
}