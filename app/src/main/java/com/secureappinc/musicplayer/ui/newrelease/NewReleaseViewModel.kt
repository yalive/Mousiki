package com.secureappinc.musicplayer.ui.newrelease

import androidx.lifecycle.MutableLiveData
import com.secureappinc.musicplayer.base.BaseViewModel
import com.secureappinc.musicplayer.base.common.Resource
import com.secureappinc.musicplayer.base.common.hasItems
import com.secureappinc.musicplayer.base.common.isLoading
import com.secureappinc.musicplayer.base.common.loading
import com.secureappinc.musicplayer.data.enteties.MusicTrack
import com.secureappinc.musicplayer.repository.NewReleaseRepository
import com.secureappinc.musicplayer.ui.home.uiScope
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 **********************************
 * Created by Abdelhadi on 4/13/19.
 **********************************
 */
class NewReleaseViewModel @Inject constructor(val newReleaseRepository: NewReleaseRepository) : BaseViewModel() {

    var trendingTracks = MutableLiveData<Resource<List<MusicTrack>>>()

    fun loadTrendingMusic() = uiScope.launch(coroutineContext) {
        if (trendingTracks.hasItems() || trendingTracks.isLoading()) {
            return@launch
        }

        trendingTracks.loading()
        val result = newReleaseRepository.loadNewReleases()
        trendingTracks.value = result
    }
}