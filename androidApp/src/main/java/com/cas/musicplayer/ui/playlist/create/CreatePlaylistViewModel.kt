package com.cas.musicplayer.ui.playlist.create

import com.cas.common.viewmodel.BaseViewModel
import com.cas.musicplayer.utils.Constants
import com.cas.musicplayer.utils.uiCoroutine
import com.mousiki.shared.domain.models.MusicTrack
import com.mousiki.shared.domain.usecase.customplaylist.AddTrackToCustomPlaylistUseCase
import com.mousiki.shared.domain.usecase.library.AddSongToFavouriteUseCase

/**
 ***************************************
 * Created by Y.Abdelhadi on 4/4/20.
 ***************************************
 */
class CreatePlaylistViewModel(
    private val addTrackToCustomPlaylist: AddTrackToCustomPlaylistUseCase,
    private val addSongToFavourite: AddSongToFavouriteUseCase
) : BaseViewModel() {

    fun createPlaylist(musicTrack: MusicTrack, playlistName: String) = uiCoroutine {
        if (playlistName == Constants.FAV_PLAYLIST_NAME) {
            addSongToFavourite(musicTrack)
        } else {
            addTrackToCustomPlaylist.invoke(musicTrack, playlistName)
        }
    }
}