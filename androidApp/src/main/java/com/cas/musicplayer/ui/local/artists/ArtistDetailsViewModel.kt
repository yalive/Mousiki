package com.cas.musicplayer.ui.local.artists

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.cas.musicplayer.tmp.trigger
import com.cas.musicplayer.ui.local.artists.model.LocalArtist
import com.cas.musicplayer.ui.local.repository.LocalArtistRepository
import com.cas.musicplayer.ui.local.songs.HeaderSongsActionsItem
import com.mousiki.shared.domain.models.*
import com.mousiki.shared.player.PlaySongDelegate
import com.mousiki.shared.player.updateCurrentPlaying
import com.mousiki.shared.ui.base.BaseViewModel
import com.mousiki.shared.ui.event.Event
import kotlinx.coroutines.launch

class ArtistDetailsViewModel(
    private val localArtistRepository: LocalArtistRepository,
    private val playSongDelegate: PlaySongDelegate
) : BaseViewModel(), PlaySongDelegate by playSongDelegate {

    private val _localSongs = MutableLiveData<List<DisplayableItem>>()
    val localSongs: LiveData<List<DisplayableItem>> get() = _localSongs

    private val _albums = MutableLiveData<List<Album>>()
    val albums: LiveData<List<Album>> get() = _albums

    private val _artist = MutableLiveData<LocalArtist>()
    val artist: LiveData<LocalArtist> get() = _artist

    private val _showMultiSelection = MutableLiveData<Event<Unit>>()
    val showMultiSelection: LiveData<Event<Unit>> get() = _showMultiSelection


    fun loadArtistSongsAndAlbums(artistId: Long) {
        val artist = localArtistRepository.artist(artistId)
        _artist.value = artist
        val songsItems = artist.songs.map { song ->
            LocalSong(song).toDisplayedVideoItem()
        }

        val displayedItems = mutableListOf<DisplayableItem>().apply {
            add(HeaderSongsActionsItem(songsItems.size,
                onPlayAllTracks = {
                    if (songsItems.isEmpty()) return@HeaderSongsActionsItem
                    onClickTrack(songsItems[0].track)
                },
                onShuffleAllTracks = { onShufflePlay() },
                onMultiSelectTracks = { _showMultiSelection.trigger() }
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