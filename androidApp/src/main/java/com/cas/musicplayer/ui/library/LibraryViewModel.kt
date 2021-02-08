package com.cas.musicplayer.ui.library

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.cas.musicplayer.ui.library.model.LibraryPlaylistItem
import com.mousiki.shared.utils.Constants
import com.mousiki.shared.domain.models.*
import com.mousiki.shared.domain.usecase.customplaylist.GetCustomPlaylistsUseCase
import com.mousiki.shared.domain.usecase.customplaylist.RemoveCustomPlaylistUseCase
import com.mousiki.shared.domain.usecase.library.GetFavouriteTracksFlowUseCase
import com.mousiki.shared.domain.usecase.library.GetFavouriteTracksUseCase
import com.mousiki.shared.domain.usecase.library.GetHeavyTracksUseCase
import com.mousiki.shared.domain.usecase.recent.GetRecentlyPlayedSongsFlowUseCase
import com.mousiki.shared.player.PlaySongDelegate
import com.mousiki.shared.ui.base.BaseViewModel
import com.mousiki.shared.ui.event.Event
import com.mousiki.shared.ui.event.asEvent
import com.mousiki.shared.utils.Strings
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch


/**
 ***************************************
 * Created by Abdelhadi on 2019-11-28.
 ***************************************
 */
class LibraryViewModel(
    private val getRecentlyPlayedSongsFlow: GetRecentlyPlayedSongsFlowUseCase,
    private val getHeavyTracksFlow: GetHeavyTracksUseCase,
    private val getFavouriteTracksFlow: GetFavouriteTracksFlowUseCase,
    private val getFavouriteTracks: GetFavouriteTracksUseCase,
    private val getCustomPlaylists: GetCustomPlaylistsUseCase,
    private val removeCustomPlaylist: RemoveCustomPlaylistUseCase,
    private val strings: Strings,
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

    private fun collectRecent() = scope.launch {
        getRecentlyPlayedSongsFlow(50).collect { songs ->
            _recentSongs.postValue(tracksToDisplayableItems(songs))
        }
    }

    private fun collectFavourite() = scope.launch {
        getFavouriteTracksFlow(20).collect { songs ->
            _favouriteSongs.postValue(tracksToDisplayableItems(songs))
        }
    }

    private fun collectHeavy() = scope.launch {
        getHeavyTracksFlow(10)
            .filter { it.size >= 3 }
            .collect { songs ->
                _heavySongs.postValue(tracksToDisplayableItems(songs))
            }
    }

    fun onClickRecentTrack(track: MusicTrack, queue: List<MusicTrack>) = scope.launch {
        _onClickSong.value = Unit.asEvent()
        playTrackFromQueue(track, queue)
    }

    fun onClickHeavyTrack(track: MusicTrack, queue: List<MusicTrack>) = scope.launch {
        _onClickSong.value = Unit.asEvent()
        playTrackFromQueue(track, queue)
    }

    fun onClickFavouriteTrack(track: MusicTrack, queue: List<MusicTrack>) = scope.launch {
        _onClickSong.value = Unit.asEvent()
        playTrackFromQueue(track, queue)
    }

    fun onClickPlaylist(playlist: Playlist) {
        scope.launch {
            if (playlist.id == Constants.FAV_PLAYLIST_NAME && getFavouriteTracks().isEmpty()) {
                showToast(strings.emptyFavouriteList)
            } else {
                _onClickPlaylist.value = playlist.asEvent()
            }
        }
    }

    fun loadCustomPlaylists() = scope.launch {
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

    fun deletePlaylist(playlist: Playlist) = scope.launch {
        removeCustomPlaylist(playlist.title)
        loadCustomPlaylists()
    }

    private fun tracksToDisplayableItems(songs: List<MusicTrack>) =
        songs.map { it.toDisplayedVideoItem() }

}