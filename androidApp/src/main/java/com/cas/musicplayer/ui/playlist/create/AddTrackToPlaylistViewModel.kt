package com.cas.musicplayer.ui.playlist.create

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.mousiki.shared.ui.base.BaseViewModel
import com.mousiki.shared.utils.Constants
import com.mousiki.shared.domain.models.MusicTrack
import com.mousiki.shared.domain.models.Playlist
import com.mousiki.shared.domain.models.imgUrl
import com.mousiki.shared.domain.usecase.customplaylist.AddTrackToCustomPlaylistUseCase
import com.mousiki.shared.domain.usecase.customplaylist.GetCustomPlaylistsUseCase
import com.mousiki.shared.domain.usecase.library.AddSongToFavouriteUseCase
import com.mousiki.shared.domain.usecase.library.GetFavouriteTracksUseCase
import com.mousiki.shared.ui.event.Event
import com.mousiki.shared.ui.event.asEvent
import kotlinx.coroutines.launch

/**
 ***************************************
 * Created by Y.Abdelhadi on 4/4/20.
 ***************************************
 */
class AddTrackToPlaylistViewModel(
    private val getCustomPlaylists: GetCustomPlaylistsUseCase,
    private val addTrackToCustomPlaylist: AddTrackToCustomPlaylistUseCase,
    private val getFavouriteTracks: GetFavouriteTracksUseCase,
    private val addSongToFavourite: AddSongToFavouriteUseCase
) : BaseViewModel() {
    private lateinit var track: MusicTrack
    private val _playlists = MutableLiveData<List<Playlist>>()
    val playlists: LiveData<List<Playlist>>
        get() = _playlists

    private val _trackAddedToPlaylist = MutableLiveData<Event<Playlist>>()
    val trackAddedToPlaylist: LiveData<Event<Playlist>>
        get() = _trackAddedToPlaylist

    fun init(track: MusicTrack) {
        this.track = track
        loadCustomPlaylists()
    }

    private fun loadCustomPlaylists() = viewModelScope.launch {
        val savedPlaylists = getCustomPlaylists().toMutableList()
        val favouriteTracks = getFavouriteTracks()
        val favouriteTrack = favouriteTracks.getOrNull(0)
        savedPlaylists.add(
            0, Playlist(
                id = "",
                title = Constants.FAV_PLAYLIST_NAME,
                urlImage = favouriteTrack?.imgUrl.orEmpty(),
                itemCount = favouriteTracks.size
            )
        )
        _playlists.value = savedPlaylists
    }

    fun addTrackToPlaylist(position: Int) = viewModelScope.launch {
        val playlists = _playlists.value ?: emptyList()
        val playlist = playlists.getOrNull(position) ?: return@launch
        if (position == 0) {
            addSongToFavourite(track)
        } else {
            addTrackToCustomPlaylist.invoke(track, playlist.title)
        }
        _trackAddedToPlaylist.value = playlist.asEvent()
    }
}