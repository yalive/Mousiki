package com.cas.musicplayer.ui.library

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import com.cas.common.event.Event
import com.cas.common.event.asEvent
import com.cas.common.viewmodel.BaseViewModel
import com.cas.musicplayer.domain.model.MusicTrack
import com.cas.musicplayer.domain.model.Playlist
import com.cas.musicplayer.domain.usecase.customplaylist.GetCustomPlaylistsUseCase
import com.cas.musicplayer.domain.usecase.library.GetFavouriteTracksLiveUseCase
import com.cas.musicplayer.domain.usecase.library.GetFavouriteTracksUseCase
import com.cas.musicplayer.domain.usecase.library.GetHeavyTracksUseCase
import com.cas.musicplayer.domain.usecase.recent.GetRecentlyPlayedSongsLiveUseCase
import com.cas.musicplayer.ui.common.PlaySongDelegate
import com.cas.musicplayer.ui.home.model.DisplayedVideoItem
import com.cas.musicplayer.ui.home.model.toDisplayedVideoItem
import com.cas.musicplayer.utils.uiCoroutine
import javax.inject.Inject

/**
 ***************************************
 * Created by Abdelhadi on 2019-11-28.
 ***************************************
 */
class LibraryViewModel @Inject constructor(
    private val getRecentlyPlayedSongsLive: GetRecentlyPlayedSongsLiveUseCase,
    private val getHeavyTracks: GetHeavyTracksUseCase,
    private val getFavouriteTracksLive: GetFavouriteTracksLiveUseCase,
    private val getFavouriteTracks: GetFavouriteTracksUseCase,
    private val getCustomPlaylists: GetCustomPlaylistsUseCase,
    delegate: PlaySongDelegate
) : BaseViewModel(), PlaySongDelegate by delegate {

    private val _recentSongs = MediatorLiveData<List<DisplayedVideoItem>>()
    val recentSongs: LiveData<List<DisplayedVideoItem>> = _recentSongs

    private val _heavySongs = MediatorLiveData<List<DisplayedVideoItem>>()
    val heavySongs: LiveData<List<DisplayedVideoItem>> = _heavySongs

    private val _favouriteSongs = MediatorLiveData<List<DisplayedVideoItem>>()
    val favouriteSongs: LiveData<List<DisplayedVideoItem>> = _favouriteSongs

    private val _playlists = MediatorLiveData<List<Playlist>>()
    val playlists: LiveData<List<Playlist>> = _playlists

    private val _onClickSong = MutableLiveData<Event<Unit>>()
    val onClickSong: LiveData<Event<Unit>> = _onClickSong

    private val _onClickPlaylist = MutableLiveData<Event<Playlist>>()
    val onClickPlaylist: LiveData<Event<Playlist>> = _onClickPlaylist

    init {
        uiCoroutine {

            _favouriteSongs.addSource(getFavouriteTracksLive(10)) { songs ->
                _favouriteSongs.postValue(tracksToDisplayableItems(songs))
            }

            _recentSongs.addSource(getRecentlyPlayedSongsLive(10)) { songs ->
                _recentSongs.postValue(tracksToDisplayableItems(songs))
            }

            _heavySongs.addSource(getHeavyTracks(10)) { songs ->
                _heavySongs.postValue(tracksToDisplayableItems(songs))
            }

            loadCustomPlaylists()
        }
    }


    fun onClickRecentTrack(track: MusicTrack) = uiCoroutine {
        _onClickSong.value = Unit.asEvent()
        val tracks = _recentSongs.value?.map { it.track } ?: emptyList()
        playTrackFromQueue(track, tracks)
    }

    fun onClickHeavyTrack(track: MusicTrack) = uiCoroutine {
        _onClickSong.value = Unit.asEvent()
        val tracks = _heavySongs.value?.map { it.track } ?: emptyList()
        playTrackFromQueue(track, tracks)
    }

    fun onClickFavouriteTrack(track: MusicTrack) = uiCoroutine {
        _onClickSong.value = Unit.asEvent()
        val tracks = _heavySongs.value?.map { it.track } ?: emptyList()
        playTrackFromQueue(track, tracks)
    }

    fun onClickPlaylist(playlist: Playlist) {
        _onClickPlaylist.value = playlist.asEvent()
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

    private fun tracksToDisplayableItems(songs: List<MusicTrack>) =
        songs.map { it.toDisplayedVideoItem() }
}