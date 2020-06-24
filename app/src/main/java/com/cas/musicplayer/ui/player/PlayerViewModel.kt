package com.cas.musicplayer.ui.player

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.cas.common.viewmodel.BaseViewModel
import com.cas.musicplayer.data.config.RemoteAppConfig
import com.cas.musicplayer.domain.model.MusicTrack
import com.cas.musicplayer.domain.usecase.library.AddSongToFavouriteUseCase
import com.cas.musicplayer.domain.usecase.library.GetFavouriteTracksLiveUseCase
import com.cas.musicplayer.domain.usecase.library.RemoveSongFromFavouriteListUseCase
import com.cas.musicplayer.player.PlayerQueue
import com.cas.musicplayer.utils.uiCoroutine
import javax.inject.Inject

/**
 ***************************************
 * Created by Abdelhadi on 2019-12-06.
 ***************************************
 */

class PlayerViewModel @Inject constructor(
    private val addSongToFavourite: AddSongToFavouriteUseCase,
    private val removeSongFromFavouriteList: RemoveSongFromFavouriteListUseCase,
    private val getFavouriteTracksLive: GetFavouriteTracksLiveUseCase,
    private val appConfig: RemoteAppConfig
) : BaseViewModel() {

    private val _isLiked = MediatorLiveData<Boolean>()
    val isLiked: LiveData<Boolean> = _isLiked

    var currentPage = 0
    init {
        uiCoroutine {
            _isLiked.addSource(getFavouriteTracksLive(20)) { songs ->
                _isLiked.postValue(songs.contains(PlayerQueue.value))
            }
        }
    }

    fun makeSongAsFavourite(musicTrack: MusicTrack) = uiCoroutine {
        addSongToFavourite(musicTrack)
    }

    fun removeSongFromFavourite(musicTrack: MusicTrack) = uiCoroutine {
        removeSongFromFavouriteList(musicTrack.youtubeId)
    }

    fun bannerAdOn() = appConfig.bannerAdOn()
}