package com.cas.musicplayer.ui.player

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.viewModelScope
import com.cas.musicplayer.player.OnChangeQueue
import com.cas.musicplayer.player.PlayerQueue
import com.cas.musicplayer.ui.common.ads.AdsItem
import com.cas.musicplayer.ui.local.repository.LocalSongsRepository
import com.google.android.gms.ads.nativead.NativeAd
import com.mousiki.shared.domain.models.*
import com.mousiki.shared.domain.usecase.library.AddSongToFavouriteUseCase
import com.mousiki.shared.domain.usecase.library.GetFavouriteTracksFlowUseCase
import com.mousiki.shared.domain.usecase.library.RemoveSongFromFavouriteListUseCase
import com.mousiki.shared.domain.usecase.recent.GetRecentlyPlayedSongsUseCase
import com.mousiki.shared.ui.base.BaseViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

/**
 ***************************************
 * Created by Abdelhadi on 2019-12-06.
 ***************************************
 */

class PlayerViewModel(
    private val addSongToFavourite: AddSongToFavouriteUseCase,
    private val removeSongFromFavouriteList: RemoveSongFromFavouriteListUseCase,
    private val getFavouriteTracksFlow: GetFavouriteTracksFlowUseCase,
    private val getRecentlyPlayedSongs: GetRecentlyPlayedSongsUseCase,
    private val localSongsRepository: LocalSongsRepository
) : BaseViewModel() {

    private val _isLiked = MediatorLiveData<Boolean>()
    val isLiked: LiveData<Boolean> = _isLiked

    private val _queue = MediatorLiveData<List<DisplayableItem>>()
    val queue: LiveData<List<DisplayableItem>> = _queue

    private val nativeAds = mutableListOf<NativeAd>()
    private val queueObserver = Observer<List<Track>?> { it ->
        val newQueue = it ?: return@Observer
        val videoItems = newQueue.map { it.toDisplayedVideoItem() }
        _queue.value = getListWithAds(videoItems)
    }

    init {
        OnChangeQueue.observeForever(queueObserver)
        viewModelScope.launch {
            getFavouriteTracksFlow(50).collect { songs ->
                val any = songs.any { it.id == PlayerQueue.value?.id }
                _isLiked.postValue(any)
            }
        }
        cueRecentTrack()
    }

    private fun cueRecentTrack() = viewModelScope.launch {
        if (PlayerQueue.value != null) return@launch
        val recentTracks = getRecentlyPlayedSongs().map {
            when (it) {
                is LocalSong -> LocalSong(localSongsRepository.song(it.song.id))
                is YtbTrack -> it
            }
        }
        if (recentTracks.isNotEmpty()) {
            PlayerQueue.cueTrack(recentTracks.first(), recentTracks)
        }
    }

    fun makeSongAsFavourite(musicTrack: Track) = viewModelScope.launch {
        addSongToFavourite(musicTrack)
    }

    fun removeSongFromFavourite(track: Track) = viewModelScope.launch {
        removeSongFromFavouriteList(track.id)
    }

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

    private fun getAdsToShowFor(listSize: Int): List<NativeAd> {
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
            it is DisplayedVideoItem && it.track.id == PlayerQueue.value?.id
        } ?: return
        val targetIndex = if (next) indexCurrent + 1 else indexCurrent - 1
        val previous = _queue.value?.getOrNull(targetIndex) as? DisplayedVideoItem
        val track = previous?.track ?: return
        PlayerQueue.playTrack(track, PlayerQueue.queue.orEmpty())
    }
}