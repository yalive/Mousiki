package com.cas.musicplayer.ui.local.albums

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.cas.musicplayer.ui.local.repository.AlbumRepository
import com.cas.musicplayer.ui.local.songs.HeaderSongsActionsItem
import com.mousiki.shared.domain.models.*
import com.mousiki.shared.player.PlaySongDelegate
import com.mousiki.shared.player.updateCurrentPlaying
import com.mousiki.shared.ui.base.BaseViewModel
import kotlinx.coroutines.launch

class AlbumDetailsViewModel(
    private val albumRepository: AlbumRepository,
    private val playSongsDelegate: PlaySongDelegate
) : BaseViewModel(), PlaySongDelegate by playSongsDelegate {

    private val _localSongs = MutableLiveData<List<DisplayableItem>>()
    val localSongs: LiveData<List<DisplayableItem>>
        get() = _localSongs

    private val _album = MutableLiveData<Album>()
    val album: LiveData<Album> get() = _album

    fun loadAlbum(albumId: Long) {
        val album = albumRepository.album(albumId)
        val songsItems = album.songs.map { song ->
            LocalSong(song).toDisplayedVideoItem()
        }
        val displayedItems = mutableListOf<DisplayableItem>().apply {
            add(HeaderSongsActionsItem(songsItems.size,
                onPlayAllTracks = { onClickTrack(songsItems[0].track) },
                onShuffleAllTracks = { onShufflePlay() }
            ))
            addAll(songsItems)
        }
        _localSongs.value = displayedItems
        _album.value = album
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

        playTrackFromQueue(tracks.random(), tracks)
    }

    fun onPlaybackStateChanged() {
        val currentItems = _localSongs.value ?: return
        val updatedList = updateCurrentPlaying(currentItems)
        _localSongs.value = updatedList
    }
}