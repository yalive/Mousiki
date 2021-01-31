package com.cas.musicplayer.ui.bottomsheet

import com.cas.common.viewmodel.BaseViewModel
import com.cas.musicplayer.utils.uiCoroutine
import com.mousiki.shared.domain.models.MusicTrack
import com.mousiki.shared.domain.models.Playlist
import com.mousiki.shared.domain.usecase.customplaylist.DeleteTrackFromCustomPlaylistUseCase
import com.mousiki.shared.domain.usecase.library.AddSongToFavouriteUseCase
import com.mousiki.shared.domain.usecase.library.RemoveSongFromFavouriteListUseCase

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