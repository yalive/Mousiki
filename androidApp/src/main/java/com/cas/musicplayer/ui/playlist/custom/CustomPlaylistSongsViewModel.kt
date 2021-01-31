package com.cas.musicplayer.ui.playlist.custom

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.cas.common.extensions.valueOrNull
import com.cas.common.resource.Resource
import com.cas.common.viewmodel.BaseViewModel
import com.cas.musicplayer.ui.common.PlaySongDelegate
import com.cas.musicplayer.ui.home.model.DisplayedVideoItem
import com.cas.musicplayer.ui.home.model.toDisplayedVideoItem
import com.cas.musicplayer.utils.Constants
import com.cas.musicplayer.utils.uiCoroutine
import com.mousiki.shared.domain.models.MusicTrack
import com.mousiki.shared.domain.models.Playlist
import com.mousiki.shared.domain.usecase.customplaylist.GetCustomPlaylistTracksUseCase
import com.mousiki.shared.domain.usecase.library.GetFavouriteTracksUseCase

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

    private fun getPlaylistSongs() = uiCoroutine {
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

    fun onClickTrack(track: MusicTrack) = uiCoroutine {
        val tracks = (_songs.value as? Resource.Success)?.data?.map { it.track } ?: emptyList()
        playTrackFromQueue(track, tracks)
    }

    fun onClickTrackPlayAll() {
        uiCoroutine {
            val allSongs = _songs.valueOrNull() ?: emptyList()
            if (allSongs.isEmpty()) return@uiCoroutine
            playTrackFromQueue(allSongs.first().track, allSongs.map { it.track })
        }
    }

    fun refresh() {
        getPlaylistSongs()
    }
}