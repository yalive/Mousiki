package com.cas.musicplayer.ui.playlist.create

import com.cas.common.viewmodel.BaseViewModel
import com.cas.musicplayer.domain.model.MusicTrack
import com.cas.musicplayer.domain.usecase.customplaylist.AddTrackToCustomPlaylistUseCase
import com.cas.musicplayer.utils.uiCoroutine
import javax.inject.Inject

/**
 ***************************************
 * Created by Y.Abdelhadi on 4/4/20.
 ***************************************
 */
class CreatePlaylistViewModel @Inject constructor(
    private val addTrackToCustomPlaylist: AddTrackToCustomPlaylistUseCase
) : BaseViewModel() {

    fun createPlaylist(musicTrack: MusicTrack, playlistName: String) = uiCoroutine {
        addTrackToCustomPlaylist.invoke(musicTrack, playlistName)
    }
}