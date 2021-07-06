package com.mousiki.shared.ui.library

import com.cas.musicplayer.ui.library.model.LibraryPlaylistItem
import com.mousiki.shared.data.config.RemoteAppConfig
import com.mousiki.shared.domain.models.*
import com.mousiki.shared.domain.usecase.customplaylist.GetCustomPlaylistsUseCase
import com.mousiki.shared.domain.usecase.customplaylist.RemoveCustomPlaylistUseCase
import com.mousiki.shared.domain.usecase.library.GetFavouriteTracksFlowUseCase
import com.mousiki.shared.domain.usecase.library.GetFavouriteTracksUseCase
import com.mousiki.shared.domain.usecase.library.GetHeavyTracksFlowUseCase
import com.mousiki.shared.domain.usecase.recent.GetRecentlyPlayedSongsFlowUseCase
import com.mousiki.shared.player.PlaySongDelegate
import com.mousiki.shared.ui.base.BaseViewModel
import com.mousiki.shared.ui.event.Event
import com.mousiki.shared.ui.event.asEvent
import com.mousiki.shared.utils.CommonFlow
import com.mousiki.shared.utils.Constants
import com.mousiki.shared.utils.Strings
import com.mousiki.shared.utils.asCommonFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
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
    private val getHeavyTracksFlowFlow: GetHeavyTracksFlowUseCase,
    private val getFavouriteTracksFlow: GetFavouriteTracksFlowUseCase,
    private val getFavouriteTracks: GetFavouriteTracksUseCase,
    private val getCustomPlaylists: GetCustomPlaylistsUseCase,
    private val removeCustomPlaylist: RemoveCustomPlaylistUseCase,
    private val strings: Strings,
    private val appConfig: RemoteAppConfig,
    delegate: PlaySongDelegate
) : BaseViewModel(), PlaySongDelegate by delegate {

    private val _recentSongs = MutableStateFlow<List<DisplayedVideoItem>?>(null)
    val recentSongs: StateFlow<List<DisplayedVideoItem>?> = _recentSongs

    private val _heavySongs = MutableStateFlow<List<DisplayedVideoItem>?>(null)
    val heavySongs: StateFlow<List<DisplayedVideoItem>?> = _heavySongs

    private val _favouriteSongs = MutableStateFlow<List<DisplayedVideoItem>?>(null)
    val favouriteSongs: StateFlow<List<DisplayedVideoItem>?> = _favouriteSongs

    private val _playlists = MutableStateFlow<List<LibraryPlaylistItem>?>(null)
    val playlists: StateFlow<List<LibraryPlaylistItem>?> = _playlists

    private val _onClickSong = MutableStateFlow<Event<Unit>?>(null)
    val onClickSong: StateFlow<Event<Unit>?> = _onClickSong

    private val _onClickPlaylist = MutableStateFlow<Event<Playlist>?>(null)
    val onClickPlaylist: StateFlow<Event<Playlist>?> = _onClickPlaylist

    init {
        collectRecent()
        collectFavourite()
        collectHeavy()
    }

    private fun collectRecent() = scope.launch {
        getRecentlyPlayedSongsFlow(50).collect { songs ->
            _recentSongs.value = tracksToDisplayableItems(songs)
        }
    }

    private fun collectFavourite() = scope.launch {
        getFavouriteTracksFlow(20).collect { songs ->
            _favouriteSongs.value = tracksToDisplayableItems(songs)
        }
    }

    private fun collectHeavy() = scope.launch {
        getHeavyTracksFlowFlow(10)
            .filter { it.size >= 3 }
            .collect { songs ->
                _heavySongs.value = tracksToDisplayableItems(songs)
            }
    }

    fun onClickRecentTrack(track: Track, queue: List<Track>) = scope.launch {
        _onClickSong.value = Unit.asEvent()
        playTrackFromQueue(track, queue)
    }

    fun onClickHeavyTrack(track: Track, queue: List<Track>) = scope.launch {
        _onClickSong.value = Unit.asEvent()
        playTrackFromQueue(track, queue)
    }

    fun onClickFavouriteTrack(track: Track, queue: List<Track>) = scope.launch {
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

    fun bannerAdOn() = appConfig.libraryBannerAdOn()

    private fun tracksToDisplayableItems(songs: List<Track>) =
        songs.map { it.toDisplayedVideoItem() }


    // For iOS
    fun recentSongsFlow(): CommonFlow<List<DisplayedVideoItem>?> {
        return recentSongs.asCommonFlow()
    }

    fun heavySongsFlow(): CommonFlow<List<DisplayedVideoItem>?> {
        return heavySongs.asCommonFlow()
    }

    fun favouriteSongsFlow(): CommonFlow<List<DisplayedVideoItem>?> {
        return favouriteSongs.asCommonFlow()
    }

    fun playlistsFlow(): CommonFlow<List<LibraryPlaylistItem>?> {
        return playlists.asCommonFlow()
    }

    fun onClickSongFlow(): CommonFlow<Event<Unit>?> {
        return onClickSong.asCommonFlow()
    }

    fun onClickPlaylistFlow(): CommonFlow<Event<Playlist>?> {
        return onClickPlaylist.asCommonFlow()
    }
}