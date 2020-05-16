package com.cas.musicplayer.ui.bottomsheet

import com.cas.common.viewmodel.BaseViewModel
import com.cas.musicplayer.domain.model.MusicTrack
import com.cas.musicplayer.domain.model.Playlist
import com.cas.musicplayer.domain.usecase.customplaylist.DeleteTrackFromCustomPlaylistUseCase
import com.cas.musicplayer.domain.usecase.library.AddSongToFavouriteUseCase
import com.cas.musicplayer.domain.usecase.library.RemoveSongFromFavouriteListUseCase
import com.cas.musicplayer.utils.uiCoroutine
import javax.inject.Inject

/**
 ***************************************
 * Created by Abdelhadi on 2019-12-06.
 ***************************************
 */
class TrackOptionsViewModel @Inject constructor(
    private val addSongToFavourite: AddSongToFavouriteUseCase,
    private val removeSongFromFavouriteList: RemoveSongFromFavouriteListUseCase,
    private val deleteTrackFromCustomPlaylist: DeleteTrackFromCustomPlaylistUseCase
) : BaseViewModel() {

    fun makeSongAsFavourite(musicTrack: MusicTrack) = uiCoroutine {
        addSongToFavourite(musicTrack)
    }

    fun removeSongFromFavourite(musicTrack: MusicTrack) = uiCoroutine {
        removeSongFromFavouriteList(musicTrack.youtubeId)
    }

    fun removeSongFromPlaylist(musicTrack: MusicTrack, playlist: Playlist) = uiCoroutine {
        deleteTrackFromCustomPlaylist(musicTrack, playlist.title)
    }
}