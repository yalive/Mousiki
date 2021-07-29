package com.cas.musicplayer.ui.playlist.select

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.mousiki.shared.domain.models.Playlist
import com.mousiki.shared.domain.models.Track
import com.mousiki.shared.domain.models.editable
import com.mousiki.shared.domain.models.isFavourite
import com.mousiki.shared.domain.usecase.customplaylist.AddTrackToCustomPlaylistUseCase
import com.mousiki.shared.domain.usecase.customplaylist.GetLocalPlaylistsUseCase
import com.mousiki.shared.domain.usecase.library.AddSongToFavouriteUseCase
import com.mousiki.shared.domain.usecase.library.GetFavouriteTracksUseCase
import com.mousiki.shared.ui.base.BaseViewModel
import com.mousiki.shared.ui.event.Event
import com.mousiki.shared.ui.event.asEvent
import kotlinx.coroutines.launch

/**
 ***************************************
 * Created by Y.Abdelhadi on 4/4/20.
 ***************************************
 */
class AddTrackToPlaylistViewModel(
    private val getLocalPlaylists: GetLocalPlaylistsUseCase,
    private val addTrackToCustomPlaylist: AddTrackToCustomPlaylistUseCase,
    private val getFavouriteTracks: GetFavouriteTracksUseCase,
    private val addSongToFavourite: AddSongToFavouriteUseCase
) : BaseViewModel() {
    private lateinit var track: Track
    private val _playlists = MutableLiveData<List<Playlist>>()
    val playlists: LiveData<List<Playlist>>
        get() = _playlists

    private val _trackAddedToPlaylist = MutableLiveData<Event<Playlist>>()
    val trackAddedToPlaylist: LiveData<Event<Playlist>> get() = _trackAddedToPlaylist

    fun init(track: Track) {
        this.track = track
        loadCustomPlaylists()
    }

    private fun loadCustomPlaylists() = viewModelScope.launch {
        val savedPlaylists = getLocalPlaylists().filter { it.editable }
        _playlists.value = savedPlaylists
    }

    fun addTrackToPlaylist(playlist: Playlist) = viewModelScope.launch {
        if (playlist.isFavourite) {
            addSongToFavourite(track)
        } else {
            addTrackToCustomPlaylist.invoke(track, playlist.id.toLong())
        }
        _trackAddedToPlaylist.value = playlist.asEvent()
    }
}