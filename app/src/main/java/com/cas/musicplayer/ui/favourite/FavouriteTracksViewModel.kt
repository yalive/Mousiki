package com.cas.musicplayer.ui.favourite

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.cas.musicplayer.domain.model.MusicTrack
import com.cas.musicplayer.domain.usecase.library.GetFavouriteTracksUseCase
import com.cas.musicplayer.domain.usecase.recent.AddTrackToRecentlyPlayedUseCase
import com.cas.musicplayer.ui.BaseSongsViewModel
import com.cas.musicplayer.ui.home.model.DisplayedVideoItem
import com.cas.musicplayer.ui.home.model.toDisplayedVideoItem
import com.cas.musicplayer.utils.uiCoroutine
import javax.inject.Inject

/**
 ***************************************
 * Created by Abdelhadi on 2019-12-06.
 ***************************************
 */
class FavouriteTracksViewModel @Inject constructor(
    private val getFavouriteTracks: GetFavouriteTracksUseCase,
    addTrackToRecentlyPlayed: AddTrackToRecentlyPlayedUseCase
) : BaseSongsViewModel(addTrackToRecentlyPlayed) {

    private val _favouritesSongs = MutableLiveData<List<DisplayedVideoItem>>()
    val favouritesSongs: LiveData<List<DisplayedVideoItem>> = _favouritesSongs

    init {
        loadFavouriteSongs()
    }

    private fun loadFavouriteSongs() = uiCoroutine {
        val songs = getFavouriteTracks()
        _favouritesSongs.value = songs.map { it.toDisplayedVideoItem() }
    }

    fun onClickFavouriteTrack(track: MusicTrack) {
        val tracks = favouritesSongs.value?.map { it.track } ?: emptyList()
        playTrackFromQueue(track, tracks)
    }
}