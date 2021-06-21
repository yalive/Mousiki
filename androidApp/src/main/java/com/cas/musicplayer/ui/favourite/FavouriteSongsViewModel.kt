package com.cas.musicplayer.ui.favourite

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.mousiki.shared.ui.base.BaseViewModel
import com.mousiki.shared.domain.models.DisplayedVideoItem
import com.mousiki.shared.domain.models.MusicTrack
import com.mousiki.shared.domain.models.toDisplayedVideoItem
import com.mousiki.shared.domain.usecase.library.GetFavouriteTracksUseCase
import com.mousiki.shared.player.PlaySongDelegate
import kotlinx.coroutines.launch

/**
 ***************************************
 * Created by Abdelhadi on 2019-12-06.
 ***************************************
 */
class FavouriteSongsViewModel(
    private val getFavouriteTracks: GetFavouriteTracksUseCase,
    delegate: PlaySongDelegate
) : BaseViewModel(), PlaySongDelegate by delegate {

    private val _favouritesSongs = MutableLiveData<List<DisplayedVideoItem>>()
    val favouritesSongs: LiveData<List<DisplayedVideoItem>> = _favouritesSongs

    init {
        loadFavouriteSongs()
    }

    private fun loadFavouriteSongs() = viewModelScope.launch {
        val songs = getFavouriteTracks()
        _favouritesSongs.value = songs.map { it.toDisplayedVideoItem() }
    }

    fun onClickFavouriteTrack(track: MusicTrack) {
        val tracks = favouritesSongs.value?.map { it.track } ?: emptyList()
        viewModelScope.launch {
            playTrackFromQueue(track, tracks)
        }
    }
}