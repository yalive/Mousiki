package com.cas.musicplayer.ui.playlist.create

import com.cas.common.viewmodel.BaseViewModel
import com.mousiki.shared.domain.models.MusicTrack
import com.cas.musicplayer.domain.usecase.customplaylist.AddTrackToCustomPlaylistUseCase
import com.cas.musicplayer.domain.usecase.library.AddSongToFavouriteUseCase
import com.cas.musicplayer.utils.Constants
import com.cas.musicplayer.utils.uiCoroutine
import javax.inject.Inject

/**
 ***************************************
 * Created by Y.Abdelhadi on 4/4/20.
 ***************************************
 */
class CreatePlaylistViewModel @Inject constructor(
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