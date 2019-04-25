package com.secureappinc.musicplayer.ui.searchyoutube

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
 * Created by Abdelhadi on 4/24/19.
 **********************************
 */
class SearchYoutubeViewModel : ViewModel() {

    val videos = MutableLiveData<List<MusicTrack>>()
    val channels = MutableLiveData<List<Artist>>()
    val playlists = MutableLiveData<List<YTTrendingItem>>()

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
                val artist = Artist(item.snippet.title, "US", item.id.channelId, item.snippet.thumbnails.high.url)
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
                MusicTrack(ytTrendingItem.id, ytTrendingItem.snippet.title, ytTrendingItem.contentDetails.duration)
            tracks.add(track)
        }
        return tracks
    }
}