package com.cas.musicplayer.ui.favourite

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.cas.common.viewmodel.BaseViewModel
import com.cas.musicplayer.domain.model.MusicTrack
import com.cas.musicplayer.domain.usecase.library.GetFavouriteTracksUseCase
import com.cas.musicplayer.ui.common.PlaySongDelegate
import com.cas.musicplayer.ui.home.model.DisplayedVideoItem
import com.cas.musicplayer.ui.home.model.toDisplayedVideoItem
import com.cas.musicplayer.utils.uiCoroutine
import javax.inject.Inject

/**
 ***************************************
 * Created by Abdelhadi on 2019-12-06.
 ***************************************
 */
class FavouriteSongsViewModel @Inject constructor(
    private val getFavouriteTracks: GetFavouriteTracksUseCase,
    delegate: PlaySongDelegate
) : BaseViewModel(), PlaySongDelegate by delegate {

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
        uiCoroutine {
            playTrackFromQueue(track, tracks)
        }
    }
}