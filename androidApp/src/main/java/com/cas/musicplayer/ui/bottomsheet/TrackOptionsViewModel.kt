package com.cas.musicplayer.ui.bottomsheet

import androidx.lifecycle.viewModelScope
import com.mousiki.shared.ui.base.BaseViewModel
import com.mousiki.shared.domain.models.YtbTrack
import com.mousiki.shared.domain.models.Playlist
import com.mousiki.shared.domain.usecase.customplaylist.DeleteTrackFromCustomPlaylistUseCase
import com.mousiki.shared.domain.usecase.library.AddSongToFavouriteUseCase
import com.mousiki.shared.domain.usecase.library.RemoveSongFromFavouriteListUseCase
import kotlinx.coroutines.launch

/**
 ***************************************
 * Created by Abdelhadi on 2019-12-06.
 ***************************************
 */
class TrackOptionsViewModel(
    private val addSongToFavourite: AddSongToFavouriteUseCase,
    private val removeSongFromFavouriteList: RemoveSongFromFavouriteListUseCase,
    private val deleteTrackFromCustomPlaylist: DeleteTrackFromCustomPlaylistUseCase
) : BaseViewModel() {

    fun makeSongAsFavourite(ytbTrack: YtbTrack) = viewModelScope.launch {
        addSongToFavourite(ytbTrack)
    }

    fun removeSongFromFavourite(ytbTrack: YtbTrack) = viewModelScope.launch {
        removeSongFromFavouriteList(ytbTrack.youtubeId)
    }

    fun removeSongFromPlaylist(ytbTrack: YtbTrack, playlist: Playlist) = viewModelScope.launch {
        deleteTrackFromCustomPlaylist(ytbTrack, playlist.title)
    }
}