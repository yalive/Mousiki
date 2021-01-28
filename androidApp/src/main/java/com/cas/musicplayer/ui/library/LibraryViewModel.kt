package com.cas.musicplayer.ui.library

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.cas.common.event.Event
import com.cas.common.event.asEvent
import com.cas.common.viewmodel.BaseViewModel
import com.cas.musicplayer.R
import com.cas.musicplayer.domain.usecase.customplaylist.GetCustomPlaylistsUseCase
import com.cas.musicplayer.domain.usecase.customplaylist.RemoveCustomPlaylistUseCase
import com.cas.musicplayer.domain.usecase.library.GetFavouriteTracksFlowUseCase
import com.cas.musicplayer.domain.usecase.library.GetFavouriteTracksUseCase
import com.cas.musicplayer.domain.usecase.library.GetHeavyTracksUseCase
import com.cas.musicplayer.domain.usecase.recent.GetRecentlyPlayedSongsFlowUseCase
import com.cas.musicplayer.ui.common.PlaySongDelegate
import com.cas.musicplayer.ui.home.model.DisplayedVideoItem
import com.cas.musicplayer.ui.home.model.toDisplayedVideoItem
import com.cas.musicplayer.ui.library.model.LibraryPlaylistItem
import com.cas.musicplayer.utils.Constants
import com.cas.musicplayer.utils.uiCoroutine
import com.mousiki.shared.domain.models.MusicTrack
import com.mousiki.shared.domain.models.Playlist
import com.mousiki.shared.domain.models.imgUrl
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 ***************************************
 * Created by Abdelhadi on 2019-11-28.
 ***************************************
 */
class LibraryViewModel @Inject constructor(
    private val getRecentlyPlayedSongsFlow: GetRecentlyPlayedSongsFlowUseCase,
    private val getHeavyTracksFlow: GetHeavyTracksUseCase,
    private val getFavouriteTracksFlow: GetFavouriteTracksFlowUseCase,
    private val getFavouriteTracks: GetFavouriteTracksUseCase,
    private val getCustomPlaylists: GetCustomPlaylistsUseCase,
    private val removeCustomPlaylist: RemoveCustomPlaylistUseCase,
    delegate: PlaySongDelegate
) : BaseViewModel(), PlaySongDelegate by delegate {

    private val _recentSongs = MutableLiveData<List<DisplayedVideoItem>>()
    val recentSongs: LiveData<List<DisplayedVideoItem>> = _recentSongs

    private val _heavySongs = MutableLiveData<List<DisplayedVideoItem>>()
    val heavySongs: LiveData<List<DisplayedVideoItem>> = _heavySongs

    private val _favouriteSongs = MutableLiveData<List<DisplayedVideoItem>>()
    val favouriteSongs: LiveData<List<DisplayedVideoItem>> = _favouriteSongs

    private val _playlists = MutableLiveData<List<LibraryPlaylistItem>>()
    val playlists: LiveData<List<LibraryPlaylistItem>> = _playlists

    private val _onClickSong = MutableLiveData<Event<Unit>>()
    val onClickSong: LiveData<Event<Unit>> = _onClickSong

    private val _onClickPlaylist = MutableLiveData<Event<Playlist>>()
    val onClickPlaylist: LiveData<Event<Playlist>> = _onClickPlaylist

    init {
        collectRecent()
        collectFavourite()
        collectHeavy()
    }

    private fun collectRecent() = viewModelScope.launch {
        getRecentlyPlayedSongsFlow(50).collect { songs ->
            _recentSongs.postValue(tracksToDisplayableItems(songs))
        }
    }

    private fun collectFavourite() = viewModelScope.launch {
        getFavouriteTracksFlow(20).collect { songs ->
            _favouriteSongs.postValue(tracksToDisplayableItems(songs))
        }
    }

    private fun collectHeavy() = viewModelScope.launch {
        getHeavyTracksFlow(10)
            .filter { it.size >= 3 }
            .collect { songs ->
                _heavySongs.postValue(tracksToDisplayableItems(songs))
            }
    }

    fun onClickRecentTrack(track: MusicTrack, queue: List<MusicTrack>) = uiCoroutine {
        _onClickSong.value = Unit.asEvent()
        playTrackFromQueue(track, queue)
    }

    fun onClickHeavyTrack(track: MusicTrack, queue: List<MusicTrack>) = uiCoroutine {
        _onClickSong.value = Unit.asEvent()
        playTrackFromQueue(track, queue)
    }

    fun onClickFavouriteTrack(track: MusicTrack, queue: List<MusicTrack>) = uiCoroutine {
        _onClickSong.value = Unit.asEvent()
        playTrackFromQueue(track, queue)
    }

    fun onClickPlaylist(playlist: Playlist) {
        viewModelScope.launch {
            if (playlist.id == Constants.FAV_PLAYLIST_NAME && getFavouriteTracks().isEmpty()) {
                showToast(R.string.empty_favourite_list)
            } else {
                _onClickPlaylist.value = playlist.asEvent()
            }
        }
    }

    fun loadCustomPlaylists() = uiCoroutine {
        val savedPlaylists = getCustomPlaylists().toMutableList()
        val favouriteTracks = getFavouriteTracks()
        val favouriteTrack = favouriteTracks.getOrNull(0)
        savedPlaylists.add(
            0, Playlist(
                id = Constants.FAV_PLAYLIST_NAME,
                title = Constants.FAV_PLAYLIST_NAME,
                urlImage = favouriteTrack?.imgUrl.orEmpty(),
                itemCount = favouriteTracks.size
            )
        )
        val items: MutableList<LibraryPlaylistItem> = savedPlaylists.map {
            LibraryPlaylistItem.CustomPlaylist(it)
        }.toMutableList()
        items.add(0, LibraryPlaylistItem.NewPlaylist)
        _playlists.value = items
    }

    fun deletePlaylist(playlist: Playlist) = uiCoroutine {
        removeCustomPlaylist(playlist.title)
        loadCustomPlaylists()
    }

    private fun tracksToDisplayableItems(songs: List<MusicTrack>) =
        songs.map { it.toDisplayedVideoItem() }

}