package com.cas.musicplayer.ui.bottompanel

import com.cas.common.viewmodel.BaseViewModel
import com.cas.musicplayer.domain.model.MusicTrack
import com.cas.musicplayer.domain.usecase.library.AddSongToFavouriteUseCase
import com.cas.musicplayer.domain.usecase.library.RemoveSongFromFavouriteListUseCase
import com.cas.musicplayer.utils.uiCoroutine
import javax.inject.Inject

/**
 ***************************************
 * Created by Abdelhadi on 2019-12-06.
 ***************************************
 */

class BottomPanelViewModel @Inject constructor(
    private val addSongToFavourite: AddSongToFavouriteUseCase,
    private val removeSongFromFavouriteList: RemoveSongFromFavouriteListUseCase
) : BaseViewModel() {

    fun makeSongAsFavourite(musicTrack: MusicTrack) = uiCoroutine {
        addSongToFavourite(musicTrack)
    }

    fun removeSongFromFavourite(musicTrack: MusicTrack) = uiCoroutine {
        removeSongFromFavouriteList(musicTrack)
    }
}