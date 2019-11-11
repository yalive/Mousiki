package com.cas.musicplayer.ui.newrelease

import androidx.lifecycle.MutableLiveData
import com.cas.musicplayer.base.BaseViewModel
import com.cas.musicplayer.base.common.ResourceOld
import com.cas.musicplayer.base.common.hasItems
import com.cas.musicplayer.base.common.isLoading
import com.cas.musicplayer.base.common.loading
import com.cas.musicplayer.data.enteties.MusicTrack
import com.cas.musicplayer.repository.NewReleaseRepository
import com.cas.musicplayer.ui.home.ui.uiScope
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 **********************************
 * Created by Abdelhadi on 4/13/19.
 **********************************
 */
class NewReleaseViewModel @Inject constructor(val newReleaseRepository: NewReleaseRepository) : BaseViewModel() {

    var trendingTracks = MutableLiveData<ResourceOld<List<MusicTrack>>>()

    fun loadTrendingMusic() = uiScope.launch(coroutineContext) {
        if (trendingTracks.hasItems() || trendingTracks.isLoading()) {
            return@launch
        }

        trendingTracks.loading()
        val result = newReleaseRepository.loadNewReleases()
        trendingTracks.value = result
    }
}