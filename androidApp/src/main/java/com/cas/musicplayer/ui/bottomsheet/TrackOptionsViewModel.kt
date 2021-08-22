package com.cas.musicplayer.ui.bottomsheet

import androidx.lifecycle.viewModelScope
import com.mousiki.shared.domain.models.Playlist
import com.mousiki.shared.domain.models.Track
import com.mousiki.shared.domain.usecase.customplaylist.DeleteTrackFromCustomPlaylistUseCase
import com.mousiki.shared.domain.usecase.library.AddSongToFavouriteUseCase
import com.mousiki.shared.domain.usecase.library.RemoveSongFromFavouriteListUseCase
import com.mousiki.shared.domain.usecase.library.RemoveSongFromRecentlyPlayedUseCase
import com.mousiki.shared.ui.base.BaseViewModel
import kotlinx.coroutines.launch

/**
 ***************************************
 * Created by Abdelhadi on 2019-12-06.
 ***************************************
 */
class TrackOptionsViewModel(
    private val addSongToFavourite: AddSongToFavouriteUseCase,
    private val removeSongFromFavouriteList: RemoveSongFromFavouriteListUseCase,
    private val deleteTrackFromCustomPlaylist: DeleteTrackFromCustomPlaylistUseCase,
    private val deleteRemoveFromRecentlyPlayed: RemoveSongFromRecentlyPlayedUseCase
) : BaseViewModel() {

    fun makeSongAsFavourite(ytbTrack: Track) = viewModelScope.launch {
        addSongToFavourite(ytbTrack)
    }

    fun removeSongFromFavourite(ytbTrack: Track) = viewModelScope.launch {
        removeSongFromFavouriteList(ytbTrack.id)
    }

    fun removeSongFromPlaylist(ytbTrack: Track, playlist: Playlist) = viewModelScope.launch {
        deleteTrackFromCustomPlaylist(ytbTrack, playlist.id)
    }

    fun removeSongFromRecentlyPlayed(ytbTrack: Track) = viewModelScope.launch {
        deleteRemoveFromRecentlyPlayed(ytbTrack.id)
    }
}