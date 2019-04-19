package com.secureappinc.musicplayer.ui.artistdetail.videos

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.secureappinc.musicplayer.models.*
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
class ArtistVideosViewModel : ViewModel() {

    val searchResultList = MutableLiveData<Resource<List<MusicTrack>>>()

    fun loadArtistTracks(channelId: String) {
        searchResultList.value = Resource.loading()
        ApiManager.api.getArtistTracks(channelId).enqueue(object : Callback<YTCategoryMusicRS> {
            override fun onResponse(call: Call<YTCategoryMusicRS>, response: Response<YTCategoryMusicRS>) {
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

            override fun onFailure(call: Call<YTCategoryMusicRS>, t: Throwable) {
                searchResultList.value = Resource.error("Error loading")
            }
        })
    }


    private fun loadVideosDetails(listMusics: List<YTCategoryMusictem>) {
        val ids = mutableListOf<String>()
        for (searchItem in listMusics) {
            ids.add(searchItem.id.videoId)
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
            val track =
                MusicTrack(musicItem.id, musicItem.snippet.title, musicItem.contentDetails.duration)
            tracks.add(track)
        }
        return tracks

    }
}