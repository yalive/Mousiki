package com.secureappinc.musicplayer.ui.detailcategory.videos

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.secureappinc.musicplayer.models.YTCategoryMusicRS
import com.secureappinc.musicplayer.models.YTCategoryMusictem
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
class GenreVideosViewModel : ViewModel() {

    val searchResultList = MutableLiveData<List<MusicTrack>>()

    fun loadVideosForTopic(topicId: String) {
        ApiManager.api.getCategoryMusic(topicId, "MA").enqueue(object : Callback<YTCategoryMusicRS> {
            override fun onResponse(call: Call<YTCategoryMusicRS>, response: Response<YTCategoryMusicRS>) {
                if (response.isSuccessful) {
                    val listMusics = response.body()?.items
                    listMusics?.let {
                        loadVideosDetails(listMusics)
                    }
                }
            }

            override fun onFailure(call: Call<YTCategoryMusicRS>, t: Throwable) {
                print("")
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
                    val listMusics = response.body()?.items
                    listMusics?.let {

                        val tracks: List<MusicTrack> = createTracksListFrom(listMusics)

                        searchResultList.value = tracks

                    }
                }
            }

            override fun onFailure(call: Call<YTTrendingMusicRS?>, t: Throwable) {
                print("")
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