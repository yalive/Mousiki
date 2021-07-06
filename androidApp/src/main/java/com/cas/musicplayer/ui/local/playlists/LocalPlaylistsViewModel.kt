package com.cas.musicplayer.ui.local.playlists

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.mousiki.shared.domain.models.Playlist
import com.mousiki.shared.ui.base.BaseViewModel
import com.mousiki.shared.utils.Constants

class LocalPlaylistsViewModel : BaseViewModel() {

    private val _playlists = MutableLiveData<List<Playlist>>()

    val playlist: LiveData<List<Playlist>> get() = _playlists

    init {
        prepareLocalPlaylists()
    }

    private fun prepareLocalPlaylists() {
        val favPlaylist = Playlist(
            id = Constants.FAV_PLAYLIST_NAME,
            title = "Favourite Songs",
            itemCount = 12,
            urlImage = ""
        )
        val mostPlayedPlaylist = Playlist(
            id = Constants.MOST_PLAYED_PLAYLIST_NAME,
            title = "Most Played",
            itemCount = 8,
            urlImage = ""
        )
        val recentlyPlayedPlaylist = Playlist(
            id = Constants.RECENT_PLAYLIST_NAME,
            title = "Recently Played",
            itemCount = 102,
            urlImage = ""
        )
        _playlists.value = listOf(favPlaylist, mostPlayedPlaylist, recentlyPlayedPlaylist)
    }
}