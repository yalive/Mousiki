package com.secureappinc.musicplayer.ui.home

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.reflect.TypeToken
import com.secureappinc.musicplayer.models.*
import com.secureappinc.musicplayer.models.enteties.MusicTrack
import com.secureappinc.musicplayer.net.ApiManager
import com.secureappinc.musicplayer.utils.Utils
import com.secureappinc.musicplayer.utils.getCurrentLocale
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.concurrent.Executors


/**
 **********************************
 * Created by Abdelhadi on 4/13/19.
 **********************************
 */
class HomeViewModel : ViewModel() {


    var trendingTracks = MutableLiveData<Resource<List<MusicTrack>>>()
    var sixArtistResources = MutableLiveData<Resource<List<Artist>>>()

    fun loadTrendingMusic() {

        val oldValue = trendingTracks.value
        if (oldValue?.status == Status.SUCCESS && oldValue.data != null && oldValue.data!!.isNotEmpty()) {
            return
        }

        ApiManager.api.getTrending(25, getCurrentLocale()).enqueue(object : Callback<YTTrendingMusicRS> {
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


    fun loadArtists(countryCode: String) {
        val oldValue = sixArtistResources.value
        if (oldValue?.data != null && oldValue.data!!.isNotEmpty()) {
            return
        }

        sixArtistResources.postValue(Resource.loading())
        Executors.newSingleThreadExecutor().execute {
            val json = Utils.loadStringJSONFromAsset("artists.json")
            val artists = ApiManager.gson.fromJson<List<Artist>>(json, object : TypeToken<List<Artist>>() {}.type)

            // Filter 6 artist by country
            val sixeArtist = artists.filter { it.countryCode.equals(countryCode, true) }.shuffled().take(6)

            if (sixeArtist.size < 6) {
                // Request US
                loadArtists("US")
                return@execute
            }

            sixArtistResources.postValue(Resource.success(sixeArtist))

            val ids = mutableListOf<String>()
            for (searchItem in sixeArtist) {
                ids.add(searchItem.channelId)
            }

            val idsStr = ids.joinToString()

            loadChannelsImages(idsStr)
        }
    }

    fun loadChannelsImages(ids: String) {
        ApiManager.api.getArtistsImages(ids).enqueue(object : Callback<YTTrendingMusicRS?> {
            override fun onFailure(call: Call<YTTrendingMusicRS?>, t: Throwable) {
                //sixArtistResources.value = Resource.error("Error")
            }

            override fun onResponse(call: Call<YTTrendingMusicRS?>, response: Response<YTTrendingMusicRS?>) {
                if (response.isSuccessful && response.body() != null) {
                    val items = response.body()!!.items

                    var newList = sixArtistResources.value!!.data!!

                    for (artist in newList) {
                        val foundItem = items.find { it.id == artist.channelId }
                        artist.urlImage = foundItem?.snippet?.thumbnails?.high?.url ?: ""
                    }

                    sixArtistResources.postValue(Resource.success(newList))

                } else {
                    //sixArtistResources.value = Resource.error("Error")
                }
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