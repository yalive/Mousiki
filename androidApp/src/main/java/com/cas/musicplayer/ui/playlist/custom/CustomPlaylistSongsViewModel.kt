package com.cas.musicplayer.ui.playlist.custom

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.cas.musicplayer.tmp.valueOrNull
import com.mousiki.shared.domain.models.*
import com.mousiki.shared.domain.usecase.customplaylist.GetCustomPlaylistTracksUseCase
import com.mousiki.shared.domain.usecase.library.GetFavouriteTracksUseCase
import com.mousiki.shared.domain.usecase.library.GetHeavyTracksUseCase
import com.mousiki.shared.domain.usecase.recent.GetRecentlyPlayedSongsUseCase
import com.mousiki.shared.player.PlaySongDelegate
import com.mousiki.shared.player.updateCurrentPlaying
import com.mousiki.shared.ui.base.BaseViewModel
import com.mousiki.shared.ui.resource.Resource
import com.mousiki.shared.utils.Constants
import kotlinx.coroutines.launch

/**
 ***************************************
 * Created by Y.Abdelhadi on 4/5/20.
 ***************************************
 */
class CustomPlaylistSongsViewModel(
    private val getCustomPlaylistTracks: GetCustomPlaylistTracksUseCase,
    private val getFavouriteTracks: GetFavouriteTracksUseCase,
    private val getRecentlyPlayedSongs: GetRecentlyPlayedSongsUseCase,
    private val getHeavyTracks: GetHeavyTracksUseCase,
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
        val tracks = when (playlist.id) {
            Constants.FAV_PLAYLIST_NAME -> getFavouriteTracks(100)
            Constants.RECENT_PLAYLIST_NAME -> getRecentlyPlayedSongs()
            Constants.MOST_PLAYED_PLAYLIST_NAME -> getHeavyTracks(100)
            else -> getCustomPlaylistTracks(playlist.title)
        }
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