package com.cas.musicplayer.ui.local.playlists

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.mousiki.shared.domain.models.Playlist
import com.mousiki.shared.ui.base.BaseViewModel

class LocalPlaylistsViewModel : BaseViewModel() {

    private val _playlists = MutableLiveData<List<Playlist>>()

    val playlist: LiveData<List<Playlist>> get() = _playlists

    init {
        prepareLocalPlaylists()
    }

    private fun prepareLocalPlaylists() {
        val favPlaylist = Playlist("fav", "Favourite Songs", 12, "")
        val mostPlayedPlaylist = Playlist("most", "Most Played", 8, "")
        val recentlyPlayedPlaylist = Playlist("recent", "Recently Played", 102, "")

        _playlists.value = listOf(favPlaylist, mostPlayedPlaylist, recentlyPlayedPlaylist)
    }
}