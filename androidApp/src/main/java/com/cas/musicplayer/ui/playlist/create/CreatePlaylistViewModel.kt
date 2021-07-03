package com.cas.musicplayer.ui.playlist.create

import androidx.lifecycle.viewModelScope
import com.mousiki.shared.ui.base.BaseViewModel
import com.mousiki.shared.utils.Constants
import com.mousiki.shared.domain.models.YtbTrack
import com.mousiki.shared.domain.usecase.customplaylist.AddTrackToCustomPlaylistUseCase
import com.mousiki.shared.domain.usecase.library.AddSongToFavouriteUseCase
import kotlinx.coroutines.launch

/**
 ***************************************
 * Created by Y.Abdelhadi on 4/4/20.
 ***************************************
 */
class CreatePlaylistViewModel(
    private val addTrackToCustomPlaylist: AddTrackToCustomPlaylistUseCase,
    private val addSongToFavourite: AddSongToFavouriteUseCase
) : BaseViewModel() {

    fun createPlaylist(ytbTrack: YtbTrack, playlistName: String) = viewModelScope.launch {
        if (playlistName == Constants.FAV_PLAYLIST_NAME) {
            addSongToFavourite(ytbTrack)
        } else {
            addTrackToCustomPlaylist.invoke(ytbTrack, playlistName)
        }
    }
}