package com.secureappinc.musicplayer.ui.artistdetail.playlists

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.secureappinc.musicplayer.models.Resource
import com.secureappinc.musicplayer.models.YTTrendingItem
import com.secureappinc.musicplayer.models.YTTrendingMusicRS
import com.secureappinc.musicplayer.net.ApiManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 **********************************
 * Created by Abdelhadi on 4/12/19.
 **********************************
 */
class ArtistPlaylistsViewModel : ViewModel() {

    val searchResultList = MutableLiveData<Resource<List<YTTrendingItem>>>()


    fun loadPlaylist(channelId: String) {
        val oldValue = searchResultList.value
        if (oldValue?.data != null && oldValue.data!!.isNotEmpty()) {
            return
        }
        searchResultList.value = Resource.loading()
        ApiManager.api.getPlaylist(channelId, "MA").enqueue(object : Callback<YTTrendingMusicRS> {
            override fun onResponse(call: Call<YTTrendingMusicRS>, response: Response<YTTrendingMusicRS>) {
                if (response.isSuccessful) {
                    val listMusics = response.body()?.items
                    if (listMusics != null) {
                        searchResultList.value = Resource.success(listMusics)
                    } else {
                        searchResultList.value = Resource.error("Error loading")
                    }
                } else {
                    searchResultList.value = Resource.error("Error")
                }
            }

            override fun onFailure(call: Call<YTTrendingMusicRS>, t: Throwable) {
                searchResultList.value = Resource.error("Error")
            }
        })
    }
}