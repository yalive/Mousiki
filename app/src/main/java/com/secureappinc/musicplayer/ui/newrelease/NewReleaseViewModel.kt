package com.secureappinc.musicplayer.ui.newrelease

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.secureappinc.musicplayer.models.Resource
import com.secureappinc.musicplayer.models.Status
import com.secureappinc.musicplayer.models.YTTrendingItem
import com.secureappinc.musicplayer.models.YTTrendingMusicRS
import com.secureappinc.musicplayer.models.enteties.MusicTrack
import com.secureappinc.musicplayer.net.ApiManager
import com.secureappinc.musicplayer.utils.getCurrentLocale
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 **********************************
 * Created by Abdelhadi on 4/13/19.
 **********************************
 */
class NewReleaseViewModel : ViewModel() {


    var trendingTracks = MutableLiveData<Resource<List<MusicTrack>>>()

    fun loadTrendingMusic() {

        val oldValue = trendingTracks.value
        if (oldValue?.status == Status.SUCCESS && oldValue.data != null && oldValue.data!!.isNotEmpty()) {
            return
        }

        trendingTracks.value = Resource.loading()

        ApiManager.api.getTrending(50, getCurrentLocale()).enqueue(object : Callback<YTTrendingMusicRS> {
            override fun onResponse(call: Call<YTTrendingMusicRS>, response: Response<YTTrendingMusicRS>) {
                if (response.isSuccessful) {
                    val listTrendingMusic = response.body()?.items

                    if (listTrendingMusic != null) {
                        val tracks: List<MusicTrack> = createTracksListFrom(listTrendingMusic)
                        trendingTracks.value = Resource.success(tracks)
                    } else {
                        trendingTracks.value = Resource.error("Error")
                    }

                }
            }

            override fun onFailure(call: Call<YTTrendingMusicRS>, t: Throwable) {
                trendingTracks.value = Resource.error("Error")
            }
        })
    }

    private fun createTracksListFrom(listTrendingYutube: List<YTTrendingItem>): List<MusicTrack> {
        val tracks: MutableList<MusicTrack> = mutableListOf()
        for (ytTrendingItem in listTrendingYutube) {
            val track =
                MusicTrack(ytTrendingItem.id, ytTrendingItem.snippetTitle(), ytTrendingItem.contentDetails.duration)

            ytTrendingItem.snippet?.urlImageOrEmpty()?.let { url ->
                track.fullImageUrl = url
            }
            tracks.add(track)
        }
        return tracks
    }
}