package com.cas.musicplayer.ui.local.artists

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.cas.musicplayer.ui.local.repository.LocalArtistRepository
import com.mousiki.shared.domain.models.*
import com.mousiki.shared.player.PlaySongDelegate
import com.mousiki.shared.player.updateCurrentPlaying
import com.mousiki.shared.ui.base.BaseViewModel
import kotlinx.coroutines.launch

class ArtistDetailsViewModel(
    private val localArtistRepository: LocalArtistRepository,
    private val playSongDelegate: PlaySongDelegate
) : BaseViewModel(), PlaySongDelegate by playSongDelegate {

    private val _localSongs = MutableLiveData<List<DisplayableItem>>()
    val localSongs: LiveData<List<DisplayableItem>> get() = _localSongs

    private val _albums = MutableLiveData<List<Album>>()
    val albums: LiveData<List<Album>> get() = _albums

    private val _artistName = MutableLiveData<String>()
    val artistName: LiveData<String> get() = _artistName

    fun loadArtistSongsAndAlbums(artistId: Long) {
        val artist = localArtistRepository.artist(artistId)
        _artistName.value = artist.name
        val songsItems = artist.songs.map { song ->
            LocalSong(song).toDisplayedVideoItem()
        }

        val displayedItems = mutableListOf<DisplayableItem>().apply {
            addAll(songsItems)
            add(HorizontalAlbumsItem("Featured in", artist.albums))
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