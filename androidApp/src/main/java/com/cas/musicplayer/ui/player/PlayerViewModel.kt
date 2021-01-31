package com.cas.musicplayer.ui.player

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.viewModelScope
import com.cas.common.viewmodel.BaseViewModel
import com.cas.musicplayer.player.OnChangeQueue
import com.cas.musicplayer.player.PlayerQueue
import com.cas.musicplayer.ui.common.ads.AdsItem
import com.cas.musicplayer.ui.home.model.DisplayedVideoItem
import com.cas.musicplayer.ui.home.model.toDisplayedVideoItem
import com.cas.musicplayer.utils.uiCoroutine
import com.google.android.gms.ads.formats.UnifiedNativeAd
import com.mousiki.shared.data.config.RemoteAppConfig
import com.mousiki.shared.domain.models.DisplayableItem
import com.mousiki.shared.domain.models.MusicTrack
import com.mousiki.shared.domain.usecase.library.AddSongToFavouriteUseCase
import com.mousiki.shared.domain.usecase.library.GetFavouriteTracksFlowUseCase
import com.mousiki.shared.domain.usecase.library.RemoveSongFromFavouriteListUseCase
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filter

/**
 ***************************************
 * Created by Abdelhadi on 2019-12-06.
 ***************************************
 */

class PlayerViewModel(
    private val addSongToFavourite: AddSongToFavouriteUseCase,
    private val removeSongFromFavouriteList: RemoveSongFromFavouriteListUseCase,
    private val getFavouriteTracksFlow: GetFavouriteTracksFlowUseCase,
    private val appConfig: RemoteAppConfig
) : BaseViewModel() {

    private val _isLiked = MediatorLiveData<Boolean>()
    val isLiked: LiveData<Boolean> = _isLiked

    private val _queue = MediatorLiveData<List<DisplayableItem>>()
    val queue: LiveData<List<DisplayableItem>> = _queue

    private val nativeAds = mutableListOf<UnifiedNativeAd>()
    private val queueObserver = Observer<List<MusicTrack>?> { newQueue ->
        newQueue?.let {
            val videoItems = newQueue.map { it.toDisplayedVideoItem() }
            _queue.value = getListWithAds(videoItems)
        }
    }

    init {
        OnChangeQueue.observeForever(queueObserver)
        viewModelScope.launch {
            getFavouriteTracksFlow(50).collect { songs ->
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

    fun prepareAds() {
        /* viewModelScope.launch {
             val ads = loadMultipleNativeAdWithMediation(3)
             nativeAds.clear()
             nativeAds.addAll(ads)
         }*/
    }

    override fun onCleared() {
        OnChangeQueue.removeObserver(queueObserver)
        super.onCleared()
    }

    private fun getListWithAds(items: List<DisplayableItem>): List<DisplayableItem> {
        val ads = getAdsToShowFor(items.size).map { AdsItem(it) }
        val offset = if (ads.isNotEmpty()) items.size / ads.size else 0
        if (offset < 3) return items
        val songsList = items.toMutableList()
        var index = offset
        ads.forEach { adsItem ->
            songsList.add(index, adsItem)
            index += offset + 1
        }
        return songsList
    }

    private fun getAdsToShowFor(listSize: Int): List<UnifiedNativeAd> {
        return if (listSize < 20 && nativeAds.size > 2) {
            nativeAds.subList(0, 2)
        } else nativeAds
    }

    fun isAdsItem(position: Int): Boolean {
        return _queue.value?.getOrNull(position) is AdsItem
    }

    fun playNext() {
        PlayerQueue.playNextTrack()
    }

    fun playPrevious() {
        PlayerQueue.playPreviousTrack()
    }

    fun swipeRight() {
        // Play previous
        doOnSwipe(false)
    }

    fun swipeLeft() {
        // Play next
        doOnSwipe(true)
    }

    private fun doOnSwipe(next: Boolean) {
        val indexCurrent = _queue.value?.indexOfFirst {
            it is DisplayedVideoItem && it.track.youtubeId == PlayerQueue.value?.youtubeId
        } ?: return
        val targetIndex = if (next) indexCurrent + 1 else indexCurrent - 1
        val previous = _queue.value?.getOrNull(targetIndex) as? DisplayedVideoItem
        val track = previous?.track ?: return
        PlayerQueue.playTrack(track, PlayerQueue.queue.orEmpty())
    }
}