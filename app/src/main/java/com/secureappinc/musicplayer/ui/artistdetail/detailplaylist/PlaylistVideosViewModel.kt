package com.secureappinc.musicplayer.ui.artistdetail.detailplaylist

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.secureappinc.musicplayer.models.Resource
import com.secureappinc.musicplayer.models.YTTrendingItem
import com.secureappinc.musicplayer.models.YTTrendingMusicRS
import com.secureappinc.musicplayer.models.enteties.MusicTrack
import com.secureappinc.musicplayer.net.ApiManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 **********************************
 * Created by Abdelhadi on 4/12/19.
 **********************************
 */
class PlaylistVideosViewModel : ViewModel() {

    val searchResultList = MutableLiveData<Resource<List<MusicTrack>>>()

    fun getPlaylistVideos(playlistId: String) {

        val oldValue = searchResultList.value
        if (oldValue?.data != null && oldValue.data!!.isNotEmpty()) {
            return
        }

        searchResultList.value = Resource.loading()
        ApiManager.api.getPlaylistVideos(playlistId, "50").enqueue(object : Callback<YTTrendingMusicRS> {
            override fun onResponse(call: Call<YTTrendingMusicRS>, response: Response<YTTrendingMusicRS>) {
                if (response.isSuccessful) {
                    val listMusics = response.body()?.items
                    if (listMusics != null) {
                        loadVideosDetails(listMusics)
                    } else {
                        searchResultList.value = Resource.error("Error loading")
                    }
                } else {
                    searchResultList.value = Resource.error("Error loading")
                }
            }

            override fun onFailure(call: Call<YTTrendingMusicRS>, t: Throwable) {
                searchResultList.value = Resource.error("Error loading")
            }
        })
    }


    private fun loadVideosDetails(listMusics: List<YTTrendingItem>) {
        val ids = mutableListOf<String>()
        for (searchItem in listMusics) {
            ids.add(searchItem.contentDetails.videoId)
        }

        val idsStr = ids.joinToString()

        ApiManager.api.getCategoryMusicDetail(idsStr).enqueue(object : Callback<YTTrendingMusicRS?> {

            override fun onResponse(call: Call<YTTrendingMusicRS?>, response: Response<YTTrendingMusicRS?>) {
                if (response.isSuccessful) {
                    val videosDetailsList = response.body()?.items

                    if (videosDetailsList != null) {
                        val tracks: List<MusicTrack> = createTracksListFrom(videosDetailsList)

                        searchResultList.value = Resource.success(tracks)
                    } else {
                        searchResultList.value = Resource.error("Error loading")
                    }

                } else {
                    searchResultList.value = Resource.error("Error loading")
                }
            }

            override fun onFailure(call: Call<YTTrendingMusicRS?>, t: Throwable) {
                searchResultList.value = Resource.error("Error loading")
            }
        })
    }

    private fun createTracksListFrom(listMusics: List<YTTrendingItem>): List<MusicTrack> {
        val tracks: MutableList<MusicTrack> = mutableListOf()
        for (musicItem in listMusics) {
            if (musicItem.contentDetails == null) {
                continue
            }
            val track =
                MusicTrack(musicItem.id, musicItem.snippet.title, musicItem.contentDetails.duration)
            tracks.add(track)
        }
        return tracks

    }
}