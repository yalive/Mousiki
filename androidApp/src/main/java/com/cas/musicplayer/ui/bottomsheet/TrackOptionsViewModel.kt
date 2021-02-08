package com.cas.musicplayer.ui.bottomsheet

import androidx.lifecycle.viewModelScope
import com.mousiki.shared.ui.base.BaseViewModel
import com.mousiki.shared.domain.models.MusicTrack
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

    fun makeSongAsFavourite(musicTrack: MusicTrack) = viewModelScope.launch {
        addSongToFavourite(musicTrack)
    }

    fun removeSongFromFavourite(musicTrack: MusicTrack) = viewModelScope.launch {
        removeSongFromFavouriteList(musicTrack.youtubeId)
    }

    fun removeSongFromPlaylist(musicTrack: MusicTrack, playlist: Playlist) = viewModelScope.launch {
        deleteTrackFromCustomPlaylist(musicTrack, playlist.title)
    }
}