package com.secureappinc.musicplayer.ui.home

import androidx.lifecycle.MutableLiveData
import com.secureappinc.musicplayer.base.BaseViewModel
import com.secureappinc.musicplayer.data.enteties.MusicTrack
import com.secureappinc.musicplayer.data.models.*
import com.secureappinc.musicplayer.repository.HomeRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

val bgContext = Dispatchers.IO
val uiContext = Dispatchers.Main

val uiScope = CoroutineScope(uiContext)

/**
 **********************************
 * Created by Abdelhadi on 4/13/19.
 **********************************
 */
class HomeViewModel(val homeRepository: HomeRepository) : BaseViewModel() {

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

