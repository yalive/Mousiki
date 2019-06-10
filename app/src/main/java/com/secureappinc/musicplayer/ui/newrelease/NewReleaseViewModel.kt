package com.secureappinc.musicplayer.ui.newrelease

import androidx.lifecycle.MutableLiveData
import com.secureappinc.musicplayer.base.BaseViewModel
import com.secureappinc.musicplayer.data.models.Resource
import com.secureappinc.musicplayer.data.models.Status
import com.secureappinc.musicplayer.data.models.YTTrendingItem
import com.secureappinc.musicplayer.data.enteties.MusicTrack
import com.secureappinc.musicplayer.ui.home.bgContext
import com.secureappinc.musicplayer.ui.home.uiScope
import com.secureappinc.musicplayer.utils.getCurrentLocale
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 **********************************
 * Created by Abdelhadi on 4/13/19.
 **********************************
 */
class NewReleaseViewModel : BaseViewModel() {


    var trendingTracks = MutableLiveData<Resource<List<MusicTrack>>>()

    fun loadTrendingMusic() {

        val oldValue = trendingTracks.value
        if (oldValue?.status == Status.SUCCESS && oldValue.data != null && oldValue.data!!.isNotEmpty()) {
            return
        }

        trendingTracks.value = Resource.loading()

        uiScope.launch(coroutineContext) {
            try {
                val musicRS = youtubeService().getTrending(50, getCurrentLocale())
                val tracks = createTracksListFrom(musicRS.items)
                trendingTracks.value = Resource.success(tracks)
            } catch (e: Exception) {
                trendingTracks.value = Resource.error("Error")
            }
        }
    }

    private suspend fun createTracksListFrom(items: List<YTTrendingItem>): List<MusicTrack> = withContext(bgContext) {
        items.map {
            val track = MusicTrack(it.id, it.snippetTitle(), it.contentDetails.duration)
            it.snippet?.urlImageOrEmpty()?.let { url ->
                track.fullImageUrl = url
            }
            track
        }
    }
}