package com.secureappinc.musicplayer.ui.artists

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.reflect.TypeToken
import com.secureappinc.musicplayer.models.Artist
import com.secureappinc.musicplayer.models.Resource
import com.secureappinc.musicplayer.models.YTTrendingMusicRS
import com.secureappinc.musicplayer.net.ApiManager
import com.secureappinc.musicplayer.utils.Utils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.concurrent.Executors

/**
 **********************************
 * Created by Abdelhadi on 4/16/19.
 **********************************
 */
class ArtistsViewModel : ViewModel() {

    var artistResources = MutableLiveData<Resource<List<Artist>>>()

    val pageSize = 15

    fun loadAllArtists() {

        if (artistResources.value != null && artistResources.value?.data != null && artistResources.value?.data!!.size > 0) {
            return
        }

        artistResources.postValue(Resource.loading())

        Executors.newSingleThreadExecutor().execute {

            val json = Utils.loadStringJSONFromAsset("artists.json")
            val artists = ApiManager.gson.fromJson<List<Artist>>(json, object : TypeToken<List<Artist>>() {}.type)

            val distinctBy = artists.distinctBy { artist -> artist.channelId }

            val sortedBy = distinctBy.sortedBy { artist -> artist.name }

            artistResources.postValue(Resource.success(sortedBy))

            loadImages(sortedBy)
        }
    }

    private fun loadImages(artists: List<Artist>) {


        val numberOfTenGroups = artists.size / pageSize
        val rest = artists.size % pageSize

        for (i in 0 until numberOfTenGroups) {

            val ids = mutableListOf<String>()
            val subList = artists.subList(i * pageSize, (i + 1) * pageSize)

            for (artist in subList) {
                ids.add(artist.channelId)
            }

            val idsStr = ids.joinToString()

            loadArtistsImages(idsStr)
        }


        // Load the rest
        val lastiIds = mutableListOf<String>()
        val subList = artists.subList(numberOfTenGroups * pageSize, numberOfTenGroups * pageSize + rest)

        for (artist in subList) {
            lastiIds.add(artist.channelId)
        }

        val idsStr = lastiIds.joinToString()

        loadArtistsImages(idsStr)
    }


    private fun loadArtistsImages(ids: String) {
        ApiManager.api.getArtistsImages(ids).enqueue(object : Callback<YTTrendingMusicRS?> {
            override fun onFailure(call: Call<YTTrendingMusicRS?>, t: Throwable) {
                Log.d("", "")
            }

            override fun onResponse(call: Call<YTTrendingMusicRS?>, response: Response<YTTrendingMusicRS?>) {
                if (response.isSuccessful && response.body() != null) {

                    val items = response.body()!!.items

                    val newList = artistResources.value!!.data!!

                    for (artist in newList) {
                        val foundItem = items.find { it.id == artist.channelId }
                        artist.urlImage = foundItem?.snippet?.thumbnails?.high?.url ?: artist.urlImage
                    }

                    artistResources.postValue(Resource.success(newList))
                }
            }
        })
    }
}