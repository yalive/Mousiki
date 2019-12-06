package com.cas.musicplayer.ui.library

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.cas.common.viewmodel.BaseViewModel
import com.cas.musicplayer.domain.model.MusicTrack
import com.cas.musicplayer.domain.usecase.library.GetFavouriteTracksUseCase
import com.cas.musicplayer.domain.usecase.library.GetHeavyTracksUseCase
import com.cas.musicplayer.domain.usecase.recent.GetRecentlyPlayedSongsUseCase
import com.cas.musicplayer.player.PlayerQueue
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
    private val getRecentlyPlayedSongs: GetRecentlyPlayedSongsUseCase,
    private val getHeavyTracks: GetHeavyTracksUseCase,
    private val getFavouriteTracks: GetFavouriteTracksUseCase
) : BaseViewModel() {

    private val _recentSongs = MutableLiveData<List<DisplayedVideoItem>>()
    val recentSongs: LiveData<List<DisplayedVideoItem>> = _recentSongs

    private val _heavySongs = MutableLiveData<List<DisplayedVideoItem>>()
    val heavySongs: LiveData<List<DisplayedVideoItem>> = _heavySongs

    private val _favouritesSongs = MutableLiveData<List<DisplayedVideoItem>>()
    val favouritesSongs: LiveData<List<DisplayedVideoItem>> = _favouritesSongs

    init {
        loadRecentlyPlayedSongs()
        loadHeavyTrackList()
        loadFavouriteSongs()
    }

    private fun loadRecentlyPlayedSongs() = uiCoroutine {
        val songs = getRecentlyPlayedSongs()
        _recentSongs.value = tracksToDisplayableItems(songs)
    }

    private fun loadHeavyTrackList() = uiCoroutine {
        val songs = getHeavyTracks()
        _heavySongs.value = tracksToDisplayableItems(songs)
    }

    private fun loadFavouriteSongs() = uiCoroutine {
        val songs = getFavouriteTracks()
        _favouritesSongs.value = tracksToDisplayableItems(songs)
    }


    fun onClickRecentTrack(track: MusicTrack) {
        val tracks = _recentSongs.value?.map { it.track } ?: emptyList()
        playTrack(track, tracks)
    }

    fun onClickHeavyTrack(track: MusicTrack) {
        val tracks = _heavySongs.value?.map { it.track } ?: emptyList()
        playTrack(track, tracks)
    }

    fun onClickFavouriteTrack(track: MusicTrack) {
        val tracks = _heavySongs.value?.map { it.track } ?: emptyList()
        playTrack(track, tracks)
    }

    private fun playTrack(track: MusicTrack, queue: List<MusicTrack>) {
        PlayerQueue.playTrack(track, queue)
    }

    private fun tracksToDisplayableItems(songs: List<MusicTrack>) = songs.map { it.toDisplayedVideoItem() }

}