package com.cas.musicplayer.ui.local.playlists

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.mousiki.shared.domain.models.Playlist
import com.mousiki.shared.domain.usecase.library.GetFavouriteTracksFlowUseCase
import com.mousiki.shared.domain.usecase.library.GetHeavyTracksFlowUseCase
import com.mousiki.shared.domain.usecase.recent.GetRecentlyPlayedSongsFlowUseCase
import com.mousiki.shared.ui.base.BaseViewModel
import com.mousiki.shared.utils.Constants
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class LocalPlaylistsViewModel(
    private val getFavouriteTracks: GetFavouriteTracksFlowUseCase,
    private val getRecentlyPlayedSongs: GetRecentlyPlayedSongsFlowUseCase,
    private val getHeavyTracks: GetHeavyTracksFlowUseCase,
) : BaseViewModel() {

    var favPlaylist = Playlist(
        id = Constants.FAV_PLAYLIST_NAME,
        title = "Favourite Songs",
        itemCount = 0,
        urlImage = ""
    )
    var mostPlayedPlaylist = Playlist(
        id = Constants.MOST_PLAYED_PLAYLIST_NAME,
        title = "Most Played",
        itemCount = 0,
        urlImage = ""
    )
    var recentlyPlayedPlaylist = Playlist(
        id = Constants.RECENT_PLAYLIST_NAME,
        title = "Recently Played",
        itemCount = 0,
        urlImage = ""
    )

    private val _playlists = MutableLiveData<List<Playlist>>()
    val playlist: LiveData<List<Playlist>> get() = _playlists

    init {
        prepareLocalPlaylists()
    }

    private fun prepareLocalPlaylists() = viewModelScope.launch {
        _playlists.value = listOf(favPlaylist, mostPlayedPlaylist, recentlyPlayedPlaylist)
        launch {
            getFavouriteTracks(300).collect {
                favPlaylist = favPlaylist.copy(itemCount = it.size)
                _playlists.value = listOf(favPlaylist, mostPlayedPlaylist, recentlyPlayedPlaylist)
            }
        }

        launch {
            getRecentlyPlayedSongs(300).collect {
                recentlyPlayedPlaylist = recentlyPlayedPlaylist.copy(itemCount = it.size)
                _playlists.value = listOf(favPlaylist, mostPlayedPlaylist, recentlyPlayedPlaylist)
            }
        }

        launch {
            getHeavyTracks(100).collect {
                mostPlayedPlaylist = mostPlayedPlaylist.copy(itemCount = it.size)
                _playlists.value = listOf(favPlaylist, mostPlayedPlaylist, recentlyPlayedPlaylist)
            }
        }
    }
}