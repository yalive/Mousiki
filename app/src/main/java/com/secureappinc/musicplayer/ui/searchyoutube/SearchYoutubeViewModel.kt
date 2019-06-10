package com.secureappinc.musicplayer.ui.searchyoutube

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.secureappinc.musicplayer.data.models.*
import com.secureappinc.musicplayer.data.enteties.MusicTrack
import com.secureappinc.musicplayer.net.ApiManager
import com.secureappinc.musicplayer.utils.getCurrentLocale
import com.secureappinc.musicplayer.utils.getLanguage
import okhttp3.ResponseBody
import org.json.JSONArray
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 **********************************
 * Created by Abdelhadi on 4/24/19.
 **********************************
 */
class SearchYoutubeViewModel : ViewModel() {

    val videos = MutableLiveData<List<MusicTrack>>()
    val channels = MutableLiveData<List<Artist>>()
    val playlists = MutableLiveData<List<YTTrendingItem>>()

    val searchSuggestions = MutableLiveData<List<String>>()

    var lastQuery = ""

    fun search(query: String) {
        if (lastQuery == query && videos.value != null && channels.value != null && playlists.value != null) {
            return
        }
        lastQuery = query
        loadVideos(query)
        loadPlaylists(query)
        loadChannels(query)
    }

    fun getSuggestions(query: String) {

        val url =
            "https://clients1.google.com/complete/search?client=youtube&hl=${getLanguage()}&ql=${getCurrentLocale()}&gs_rn=23&gs_ri=youtube&ds=yt&cp=${query.length}&q=${query.replace(
                " ",
                "+"
            )}"

        val call: Call<ResponseBody> = ApiManager.api.getSuggestions(url)

        call.enqueue(object : Callback<ResponseBody?> {
            override fun onFailure(call: Call<ResponseBody?>, t: Throwable) {
                print("")
            }

            override fun onResponse(call: Call<ResponseBody?>, response: Response<ResponseBody?>) {
                try {
                    response.body()?.string()?.let { stringResponse ->
                        if (stringResponse.startsWith("window.google.ac.h")) {

                            val json =
                                stringResponse.substring(stringResponse.indexOf("(") + 1, stringResponse.indexOf(")"))

                            val jsonArray = JSONArray(json).getJSONArray(1)

                            val suggestions = mutableListOf<String>()
                            for (i in 0 until jsonArray.length()) {
                                suggestions.add(jsonArray.getJSONArray(i).getString(0))
                            }

                            searchSuggestions.value = suggestions
                        }
                    }
                } catch (e: Exception) {
                }
            }
        })
    }

    private fun loadVideos(query: String) {
        ApiManager.api.searchYoutubeMusic(query, "video", 50).enqueue(object : Callback<YTCategoryMusicRS?> {
            override fun onFailure(call: Call<YTCategoryMusicRS?>, t: Throwable) {
                print("")
            }

            override fun onResponse(call: Call<YTCategoryMusicRS?>, response: Response<YTCategoryMusicRS?>) {
                if (response.isSuccessful && response.body() != null) {
                    val items = response.body()!!.items
                    loadVideosDurations(items)
                }
            }
        })
    }

    private fun loadPlaylists(query: String) {
        ApiManager.api.searchYoutube(query, "playlist", 20).enqueue(object : Callback<YTCategoryMusicRS?> {
            override fun onFailure(call: Call<YTCategoryMusicRS?>, t: Throwable) {
                print("")
            }

            override fun onResponse(call: Call<YTCategoryMusicRS?>, response: Response<YTCategoryMusicRS?>) {
                if (response.isSuccessful && response.body() != null) {
                    val items = response.body()!!.items
                    loadPlaylistsDetail(items)
                }
            }
        })
    }

    private fun loadChannels(query: String) {
        ApiManager.api.searchYoutube(query, "channel", 15).enqueue(object : Callback<YTCategoryMusicRS?> {
            override fun onFailure(call: Call<YTCategoryMusicRS?>, t: Throwable) {
                print("")
            }

            override fun onResponse(call: Call<YTCategoryMusicRS?>, response: Response<YTCategoryMusicRS?>) {
                if (response.isSuccessful && response.body() != null) {
                    val items: List<Artist> = createArtistsFromResponse(response.body()!!.items)
                    channels.value = items
                }
            }
        })
    }

    private fun createArtistsFromResponse(items: List<YTCategoryMusictem>): List<Artist> {
        val artists: MutableList<Artist> = mutableListOf()
        for (item in items) {
            if (item.id != null) {
                val artist = Artist(item.snippet.title, "US", item.id.channelId, item.snippet.urlImageOrEmpty())
                artists.add(artist)
            }
        }
        return artists
    }

    private fun loadVideosDurations(listMusics: List<YTCategoryMusictem>) {
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
                        videos.value = tracks
                    }
                }
            }

            override fun onFailure(call: Call<YTTrendingMusicRS?>, t: Throwable) {
                print("")
            }
        })
    }

    private fun loadPlaylistsDetail(listMusics: List<YTCategoryMusictem>) {
        val ids = mutableListOf<String>()
        for (searchItem in listMusics) {
            ids.add(searchItem.id.playlistId)
        }
        val idsStr = ids.joinToString()

        ApiManager.api.getPlaylistsDetail(idsStr).enqueue(object : Callback<YTTrendingMusicRS?> {

            override fun onResponse(call: Call<YTTrendingMusicRS?>, response: Response<YTTrendingMusicRS?>) {
                if (response.isSuccessful) {
                    val videosDetailsList = response.body()?.items
                    if (videosDetailsList != null) {
                        playlists.value = videosDetailsList
                    }
                }
            }

            override fun onFailure(call: Call<YTTrendingMusicRS?>, t: Throwable) {
                print("")
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