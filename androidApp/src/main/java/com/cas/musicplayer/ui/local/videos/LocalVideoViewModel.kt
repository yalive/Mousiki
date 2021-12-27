package com.cas.musicplayer.ui.local.videos

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.cas.musicplayer.ui.common.ads.AdsItem
import com.cas.musicplayer.ui.local.repository.LocalVideosRepository
import com.cas.musicplayer.ui.local.repository.filterNotHidden
import com.mousiki.shared.ads.GetListAdsDelegate
import com.mousiki.shared.domain.models.DisplayableItem
import com.mousiki.shared.domain.models.LocalSong
import com.mousiki.shared.domain.models.Track
import com.mousiki.shared.domain.models.toDisplayedVideoItem
import com.mousiki.shared.domain.usecase.recent.AddVideoToRecentlyPlayedUseCase
import com.mousiki.shared.player.PlaySongDelegate
import com.mousiki.shared.player.updateCurrentPlaying
import com.mousiki.shared.ui.base.BaseViewModel
import com.mousiki.shared.ui.resource.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LocalVideoViewModel(
    private val localSongsRepository: LocalVideosRepository,
    private val addVideoToRecentlyPlayed: AddVideoToRecentlyPlayedUseCase,
    private val playSongsDelegate: PlaySongDelegate,
    getListAdsDelegate: GetListAdsDelegate,
) : BaseViewModel(), PlaySongDelegate by playSongsDelegate,
    GetListAdsDelegate by getListAdsDelegate {

    private val _localSongs = MutableLiveData<Resource<List<DisplayableItem>>>()
    val localSongs: LiveData<Resource<List<DisplayableItem>>>
        get() = _localSongs

    fun loadAllVideos() = viewModelScope.launch(Dispatchers.IO) {
        _localSongs.postValue(Resource.Loading)
        val songs = localSongsRepository.videos().filterNotHidden()
        val songsItems = songs.map {
            LocalSong(it).toDisplayedVideoItem()
        }

        val displayedItems = mutableListOf<DisplayableItem>().apply {
            add(HeaderVideosActionsItem(songsItems.size))
            addAll(songsItems)
        }
        _localSongs.postValue(Resource.Success(updateCurrentPlaying(displayedItems)))

        // Insert ads
        insertAds()
    }

    private suspend fun insertAds() {
        val ads = getOrAwaitNativeAds(ADS_COUNT)
        if (ads.isEmpty()) return
        val listItems = (_localSongs.value as? Resource.Success)?.data ?: return
        val items = listItems.filter { it !is AdsItem }.toMutableList()
        items.add(1, ads.first())
        if (ads.size > 1) {
            val nextAd = ads[1]
            if (items.size > 10) {
                items.add(8, nextAd)
            } else if (items.size > 5) {
                items.add(nextAd)
            }
        }
        _localSongs.postValue(Resource.Success(items))
    }

    fun onPlayVideo(track: Track) = viewModelScope.launch {
        addVideoToRecentlyPlayed(track)
    }

    companion object {
        private const val ADS_COUNT = 1
    }
}