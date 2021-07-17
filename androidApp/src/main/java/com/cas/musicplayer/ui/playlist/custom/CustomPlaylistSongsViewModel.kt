package com.cas.musicplayer.ui.playlist.custom

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.cas.musicplayer.tmp.valueOrNull
import com.mousiki.shared.domain.models.*
import com.mousiki.shared.domain.usecase.customplaylist.GetCustomPlaylistTracksUseCase
import com.mousiki.shared.domain.usecase.library.GetFavouriteTracksFlowUseCase
import com.mousiki.shared.domain.usecase.library.GetHeavyTracksFlowUseCase
import com.mousiki.shared.domain.usecase.recent.GetRecentlyPlayedSongsFlowUseCase
import com.mousiki.shared.player.PlaySongDelegate
import com.mousiki.shared.player.updateCurrentPlaying
import com.mousiki.shared.ui.base.BaseViewModel
import com.mousiki.shared.ui.resource.Resource
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

/**
 ***************************************
 * Created by Y.Abdelhadi on 4/5/20.
 ***************************************
 */
class CustomPlaylistSongsViewModel(
    private val getCustomPlaylistTracks: GetCustomPlaylistTracksUseCase,
    private val getFavouriteTracks: GetFavouriteTracksFlowUseCase,
    private val getRecentlyPlayedSongs: GetRecentlyPlayedSongsFlowUseCase,
    private val getHeavyTracks: GetHeavyTracksFlowUseCase,
    delegate: PlaySongDelegate
) : BaseViewModel(), PlaySongDelegate by delegate {
    lateinit var playlist: Playlist
        private set
    private val _songs = MutableLiveData<Resource<List<DisplayableItem>>>()
    val songs: LiveData<Resource<List<DisplayableItem>>> get() = _songs

    fun init(playlist: Playlist) {
        this.playlist = playlist
        getPlaylistSongs()
    }

    private fun getPlaylistSongs() = viewModelScope.launch {
        _songs.value = Resource.Loading
        when (playlist.type) {
            Playlist.TYPE_FAV -> getFavouriteTracks(300).collect { showTracks(it) }
            Playlist.TYPE_RECENT -> getRecentlyPlayedSongs(300).collect { showTracks(it) }
            Playlist.TYPE_HEAVY -> getHeavyTracks(300).collect { showTracks(it) }
            else -> showTracks(getCustomPlaylistTracks(playlist.id))
        }
    }

    private fun showTracks(tracks: List<Track>) {
        val items = tracks.map {
            val isCurrent = currentSong?.id == it.id
            it.toDisplayedVideoItem(
                isCurrent = isCurrent,
                isPlaying = isCurrent && isPlayingASong()
            )
        }
        _songs.value = Resource.Success(items)
    }

    fun onClickTrack(track: Track) = viewModelScope.launch {
        val tracks = (_songs.value as? Resource.Success)?.data
            ?.filterIsInstance<DisplayedVideoItem>()
            ?.map { it.track } ?: emptyList()
        playTrackFromQueue(track, tracks)
    }

    fun onClickTrackPlayAll() {
        viewModelScope.launch {
            val allSongs = _songs.valueOrNull()
                ?.filterIsInstance<DisplayedVideoItem>() ?: emptyList()
            if (allSongs.isEmpty()) return@launch
            playTrackFromQueue(allSongs.first().track, allSongs.map { it.track })
        }
    }

    fun refresh() {
        getPlaylistSongs()
    }

    fun onPlaybackStateChanged() {
        val currentItems = _songs.valueOrNull() ?: return
        val updatedList = updateCurrentPlaying(currentItems)
        _songs.value = Resource.Success(updatedList)
    }

}