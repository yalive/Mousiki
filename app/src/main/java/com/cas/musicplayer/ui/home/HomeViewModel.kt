package com.cas.musicplayer.ui.home

import androidx.lifecycle.MutableLiveData
import com.cas.musicplayer.base.BaseViewModel
import com.cas.musicplayer.base.common.Resource
import com.cas.musicplayer.base.common.hasItems
import com.cas.musicplayer.base.common.isLoading
import com.cas.musicplayer.base.common.loading
import com.cas.musicplayer.data.enteties.MusicTrack
import com.cas.musicplayer.data.models.Artist
import com.cas.musicplayer.repository.HomeRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

val bgContext = Dispatchers.IO
val uiContext = Dispatchers.Main

val uiScope = CoroutineScope(uiContext)

/**
 **********************************
 * Created by Abdelhadi on 4/13/19.
 **********************************
 */
class HomeViewModel @Inject constructor(val homeRepository: HomeRepository) : BaseViewModel() {

    var trendingTracks = MutableLiveData<Resource<List<MusicTrack>>>()

    var sixArtists = MutableLiveData<Resource<List<Artist>>>()

    fun loadTrendingMusic() = uiScope.launch(coroutineContext) {
        if (trendingTracks.hasItems() || trendingTracks.isLoading()) {
            return@launch
        }
        trendingTracks.loading()
        val result = homeRepository.loadNewReleases()
        trendingTracks.value = result
    }


    fun loadArtists(countryCode: String) = uiScope.launch(coroutineContext) {
        if (!sixArtists.hasItems() && !sixArtists.isLoading()) {
            sixArtists.loading()
            val sixArtistResult = homeRepository.loadArtists(countryCode)
            sixArtists.value = sixArtistResult
        }
    }
}

