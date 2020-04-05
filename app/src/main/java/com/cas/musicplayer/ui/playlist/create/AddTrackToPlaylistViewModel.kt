package com.cas.musicplayer.ui.playlist.create

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.cas.common.event.Event
import com.cas.common.event.asEvent
import com.cas.common.viewmodel.BaseViewModel
import com.cas.musicplayer.domain.model.MusicTrack
import com.cas.musicplayer.domain.model.Playlist
import com.cas.musicplayer.domain.usecase.customplaylist.AddTrackToCustomPlaylistUseCase
import com.cas.musicplayer.domain.usecase.customplaylist.GetCustomPlaylistsUseCase
import com.cas.musicplayer.domain.usecase.library.AddSongToFavouriteUseCase
import com.cas.musicplayer.domain.usecase.library.GetFavouriteTracksUseCase
import com.cas.musicplayer.utils.uiCoroutine
import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject

/**
 ***************************************
 * Created by Y.Abdelhadi on 4/4/20.
 ***************************************
 */
class AddTrackToPlaylistViewModel @AssistedInject constructor(
    @Assisted val track: MusicTrack,
    private val getCustomPlaylists: GetCustomPlaylistsUseCase,
    private val addTrackToCustomPlaylist: AddTrackToCustomPlaylistUseCase,
    private val getFavouriteTracks: GetFavouriteTracksUseCase,
    private val addSongToFavourite: AddSongToFavouriteUseCase
) : BaseViewModel() {

    @AssistedInject.Factory
    interface Factory {
        fun create(track: MusicTrack): AddTrackToPlaylistViewModel
    }


    private val _playlists = MutableLiveData<List<Playlist>>()
    val playlists: LiveData<List<Playlist>>
        get() = _playlists

    private val _trackAddedToPlaylist = MutableLiveData<Event<Playlist>>()
    val trackAddedToPlaylist: LiveData<Event<Playlist>>
        get() = _trackAddedToPlaylist

    init {
        loadCustomPlaylists()
    }

    private fun loadCustomPlaylists() = uiCoroutine {
        val savedPlaylists = getCustomPlaylists().toMutableList()
        val favouriteTracks = getFavouriteTracks()
        val favouriteTrack = favouriteTracks.getOrNull(0)
        savedPlaylists.add(
            0, Playlist(
                id = "",
                title = "Favourite",
                urlImage = favouriteTrack?.imgUrl ?: "",
                itemCount = favouriteTracks.size
            )
        )
        _playlists.value = savedPlaylists
    }

    fun addTrackToPlaylist(position: Int) = uiCoroutine {
        val playlists = _playlists.value ?: emptyList()
        val playlist = playlists.getOrNull(position) ?: return@uiCoroutine
        if (position == 0) {
            addSongToFavourite(track)
        } else {
            addTrackToCustomPlaylist.invoke(track, playlist.title)
        }
        _trackAddedToPlaylist.value = playlist.asEvent()
    }
}