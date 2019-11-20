package com.cas.musicplayer.repository

import com.cas.musicplayer.data.enteties.MusicTrack
import com.cas.musicplayer.data.mappers.YTBChannelToArtist
import com.cas.musicplayer.data.mappers.YTBSearchResultToVideoId
import com.cas.musicplayer.data.mappers.YTBVideoToTrack
import com.cas.musicplayer.data.mappers.toListMapper
import com.cas.musicplayer.data.models.Artist
import com.cas.musicplayer.net.NO_RESULT
import com.cas.musicplayer.net.Result
import com.cas.musicplayer.net.Result.Success
import com.cas.musicplayer.net.RetrofitRunner
import com.cas.musicplayer.net.YoutubeService
import com.cas.musicplayer.utils.Utils
import com.cas.musicplayer.utils.bgContext
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 ***************************************
 * Created by Abdelhadi on 2019-06-10.
 ***************************************
 */

@Singleton
class ArtistsRepository @Inject constructor(
    private var youtubeService: YoutubeService,
    private var gson: Gson,
    private val retrofitRunner: RetrofitRunner,
    private val trackMapper: YTBVideoToTrack,
    private val artistMapper: YTBChannelToArtist,
    private val searchMapper: YTBSearchResultToVideoId
) {

    suspend fun getArtistsChannels(ids: String): Result<List<Artist>> {
        return retrofitRunner.executeNetworkCall(artistMapper.toListMapper()) {
            youtubeService.channels(ids).items ?: emptyList()
        }
    }

    suspend fun getArtistsFromFile(): List<Artist> = withContext(bgContext) {
        val json = Utils.loadStringJSONFromAsset("artists.json")
        val artists = gson.fromJson<List<Artist>>(json, object : TypeToken<List<Artist>>() {}.type)
        val distinctBy = artists.distinctBy { artist -> artist.channelId }
        distinctBy.sortedBy { artist -> artist.name }
    }

    suspend fun getArtistTracks(artistChannelId: String): Result<List<MusicTrack>> {
        val result = retrofitRunner.executeNetworkCall(searchMapper.toListMapper()) {
            youtubeService.channelVideoIds(artistChannelId).items ?: emptyList()
        } as? Success ?: return NO_RESULT

        // 2 - Get videos
        val ids = result.data.joinToString { it.id }
        return retrofitRunner.executeNetworkCall(trackMapper.toListMapper()) {
            youtubeService.videos(ids).items ?: emptyList()
        }
    }
}