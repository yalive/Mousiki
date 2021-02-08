package com.cas.musicplayer.ui.playlist.custom

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.mousiki.shared.ui.base.BaseViewModel
import com.cas.musicplayer.tmp.valueOrNull
import com.mousiki.shared.utils.Constants
import com.mousiki.shared.domain.models.DisplayedVideoItem
import com.mousiki.shared.domain.models.MusicTrack
import com.mousiki.shared.domain.models.Playlist
import com.mousiki.shared.domain.models.toDisplayedVideoItem
import com.mousiki.shared.domain.usecase.customplaylist.GetCustomPlaylistTracksUseCase
import com.mousiki.shared.domain.usecase.library.GetFavouriteTracksUseCase
import com.mousiki.shared.player.PlaySongDelegate
import com.mousiki.shared.ui.resource.Resource
import kotlinx.coroutines.launch

/**
 ***************************************
 * Created by Y.Abdelhadi on 4/5/20.
 ***************************************
 */
class CustomPlaylistSongsViewModel(
    private val getCustomPlaylistTracks: GetCustomPlaylistTracksUseCase,
    private val getFavouriteTracks: GetFavouriteTracksUseCase,
    delegate: PlaySongDelegate
) : BaseViewModel(), PlaySongDelegate by delegate {
    lateinit var playlist: Playlist
        private set
    private val _songs = MutableLiveData<Resource<List<DisplayedVideoItem>>>()
    val songs: LiveData<Resource<List<DisplayedVideoItem>>>
        get() = _songs

    fun init(playlist: Playlist) {
        this.playlist = playlist
        getPlaylistSongs()
    }

    private fun getPlaylistSongs() = viewModelScope.launch {
        _songs.value = Resource.Loading
        val tracks = when (playlist.id) {
            Constants.FAV_PLAYLIST_NAME -> getFavouriteTracks(100)
            else -> getCustomPlaylistTracks(playlist.title)
        }
        val items = tracks.map {
            it.toDisplayedVideoItem()
        }
        _songs.value = Resource.Success(items)
    }

    fun onClickTrack(track: MusicTrack) = viewModelScope.launch {
        val tracks = (_songs.value as? Resource.Success)?.data?.map { it.track } ?: emptyList()
        playTrackFromQueue(track, tracks)
    }

    fun onClickTrackPlayAll() {
        viewModelScope.launch {
            val allSongs = _songs.valueOrNull() ?: emptyList()
            if (allSongs.isEmpty()) return@launch
            playTrackFromQueue(allSongs.first().track, allSongs.map { it.track })
        }
    }

    fun refresh() {
        getPlaylistSongs()
    }
}