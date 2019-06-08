package com.secureappinc.musicplayer.ui.home

import androidx.lifecycle.MutableLiveData
import com.google.gson.reflect.TypeToken
import com.secureappinc.musicplayer.base.BaseViewModel
import com.secureappinc.musicplayer.models.*
import com.secureappinc.musicplayer.models.enteties.MusicTrack
import com.secureappinc.musicplayer.net.ApiManager
import com.secureappinc.musicplayer.utils.Utils
import com.secureappinc.musicplayer.utils.getCurrentLocale
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

val bgContext = Dispatchers.IO
val uiContext = Dispatchers.Main

val uiScope = CoroutineScope(uiContext)

/**
 **********************************
 * Created by Abdelhadi on 4/13/19.
 **********************************
 */
class HomeViewModel : BaseViewModel() {
    var trendingTracks = MutableLiveData<Resource<List<MusicTrack>>>()
    var sixArtistResources = MutableLiveData<Resource<List<Artist>>>()

    fun loadTrendingMusic() {
        if (trendingTracks.hasItems() || trendingTracks.isLoading()) {
            return
        }
        loadNewReleasesTracks()
    }

    private fun loadNewReleasesTracks() = uiScope.launch(coroutineContext) {
        trendingTracks.value = Resource.loading()
        try {
            val musicRS = api().getTrending(25, getCurrentLocale())
            val tracks = createTracksListFrom(musicRS.items)
            trendingTracks.value = Resource.success(tracks)
        } catch (e: Exception) {
            trendingTracks.value = Resource.error("Error")
        }
    }

    fun loadArtists(countryCode: String) = uiScope.launch(coroutineContext) {
        if (!sixArtistResources.hasItems() && !sixArtistResources.isLoading()) {
            sixArtistResources.value = Resource.loading()
            val sixArtist = getSixArtists(countryCode)
            sixArtistResources.value = Resource.success(sixArtist)

            try {
                val ids = sixArtist.joinToString { it.channelId }
                val ytbRS = api().getArtistsImages(ids)
                for (artist in sixArtist) {
                    val foundItem = ytbRS.items.find { it.id == artist.channelId }
                    artist.urlImage = foundItem?.snippet?.thumbnails?.high?.url ?: ""
                }
                sixArtistResources.value = Resource.success(sixArtist)
            } catch (e: Exception) {
            }
        }
    }

    private suspend fun getSixArtists(countryCode: String): List<Artist> = withContext(bgContext) {
        val json = Utils.loadStringJSONFromAsset("artists.json")
        val artists = ApiManager.gson.fromJson<List<Artist>>(json, object : TypeToken<List<Artist>>() {}.type)

        // Filter 6 artist by country
        var sixeArtist = artists.filter { it.countryCode.equals(countryCode, true) }.shuffled().take(6)

        if (sixeArtist.size < 6) {
            // Request US
            sixeArtist = artists.filter { it.countryCode.equals("US", true) }.shuffled().take(6)
        }
        return@withContext sixeArtist
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