package com.secureappinc.musicplayer.repository

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.secureappinc.musicplayer.base.common.Resource
import com.secureappinc.musicplayer.data.enteties.MusicTrack
import com.secureappinc.musicplayer.data.mappers.YTBChannelToArtist
import com.secureappinc.musicplayer.data.mappers.YTBVideoToTrack
import com.secureappinc.musicplayer.data.mappers.toListMapper
import com.secureappinc.musicplayer.data.models.Artist
import com.secureappinc.musicplayer.net.RetrofitRunner
import com.secureappinc.musicplayer.net.YoutubeService
import com.secureappinc.musicplayer.net.toResource
import com.secureappinc.musicplayer.ui.home.bgContext
import com.secureappinc.musicplayer.utils.Utils
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 ***************************************
 * Created by Abdelhadi on 2019-06-09.
 ***************************************
 */
@Singleton
class HomeRepository @Inject constructor(
    private var youtubeService: YoutubeService,
    private var gson: Gson,
    private val retrofitRunner: RetrofitRunner,
    private val trackMapper: YTBVideoToTrack,
    private val artistMapper: YTBChannelToArtist
) {

    suspend fun loadNewReleases(): Resource<List<MusicTrack>> =
        retrofitRunner.executeNetworkCall(trackMapper.toListMapper()) {
            youtubeService.trending(25, "ma").items!!
        }.toResource()

    suspend fun loadArtists(countryCode: String): Resource<List<Artist>> {
        // Get six artist from json
        val sixArtist = getSixArtists(countryCode)

        // Get detail of artists
        val ids = sixArtist.joinToString { it.channelId }
        return retrofitRunner.executeNetworkCall(artistMapper.toListMapper()) {
            youtubeService.channels(ids).items!!
        }.toResource()
    }

    private suspend fun getSixArtists(countryCode: String): List<Artist> = withContext(bgContext) {
        val json = Utils.loadStringJSONFromAsset("artists.json")
        val artists = gson.fromJson<List<Artist>>(json, object : TypeToken<List<Artist>>() {}.type)

        // Filter 6 artist by country
        var sixeArtist = artists.filter { it.countryCode.equals(countryCode, true) }.shuffled().take(6)

        if (sixeArtist.size < 6) {
            // Request US
            sixeArtist = artists.filter { it.countryCode.equals("US", true) }.shuffled().take(6)
        }
        return@withContext sixeArtist
    }
}