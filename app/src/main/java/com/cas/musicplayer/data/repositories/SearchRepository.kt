package com.cas.musicplayer.data.repositories

import com.cas.musicplayer.domain.model.Channel
import com.cas.musicplayer.domain.model.MusicTrack
import com.cas.musicplayer.domain.model.Playlist
import com.cas.musicplayer.data.mappers.*
import com.cas.common.result.NO_RESULT
import com.cas.common.result.Result
import com.cas.common.result.Result.Success
import com.cas.musicplayer.data.net.RetrofitRunner
import com.cas.musicplayer.data.net.YoutubeService
import org.json.JSONArray
import javax.inject.Inject
import javax.inject.Singleton

/**
 ***************************************
 * Created by Abdelhadi on 2019-06-12.
 ***************************************
 */
@Singleton
class SearchRepository @Inject constructor(
    private var youtubeService: YoutubeService,
    private val retrofitRunner: RetrofitRunner,
    private val trackMapper: YTBVideoToTrack,
    private val playlistMapper: YTBPlaylistToPlaylist,
    private val channelMapper: YTBChannelToChannel,
    private val videoIdMapper: YTBSearchResultToVideoId,
    private val channelIdMapper: YTBSearchResultToChannelId,
    private val playlistIdMapper: YTBSearchResultToPlaylistId
) {

    suspend fun searchTracks(query: String): Result<List<MusicTrack>> {
        val idsResult = retrofitRunner.executeNetworkCall(videoIdMapper.toListMapper()) {
            youtubeService.searchVideoIdsByQuery(query, 50).items ?: emptyList()
        } as? Success ?: return NO_RESULT

        // 2 - Get videos
        val ids = idsResult.data.joinToString { it.id }
        return retrofitRunner.executeNetworkCall(trackMapper.toListMapper()) {
            youtubeService.videos(ids).items ?: emptyList()
        }
    }

    suspend fun searchPlaylists(query: String): Result<List<Playlist>> {
        val idsResult = retrofitRunner.executeNetworkCall(playlistIdMapper.toListMapper()) {
            youtubeService.searchItemIdsByQuery(query, "playlist", 30).items!!
        } as? Success ?: return NO_RESULT

        // 2 - Get videos
        val ids = idsResult.data.joinToString { it.id }
        val videosResult = retrofitRunner.executeNetworkCall(playlistMapper.toListMapper()) {
            youtubeService.playlists(ids).items!!
        }
        return videosResult
    }


    suspend fun searchChannels(query: String): Result<List<Channel>> {
        val idsResult = retrofitRunner.executeNetworkCall(channelIdMapper.toListMapper()) {
            youtubeService.searchItemIdsByQuery(query, "channel", 15).items ?: emptyList()
        } as? Success ?: return NO_RESULT

        val ids = idsResult.data.joinToString { it.id }
        return retrofitRunner.executeNetworkCall(channelMapper.toListMapper()) {
            youtubeService.channels(ids).items ?: emptyList()
        }
    }

    suspend fun getSuggestions(url: String): List<String> {
        try {
            val responseBody = youtubeService.suggestions(url)
            val stringResponse = responseBody.string()
            if (stringResponse.startsWith("window.google.ac.h")) {
                val json =
                    stringResponse.substring(
                        stringResponse.indexOf("(") + 1,
                        stringResponse.indexOf(")")
                    )

                val jsonArray = JSONArray(json).getJSONArray(1)

                val suggestions = mutableListOf<String>()
                for (i in 0 until jsonArray.length()) {
                    suggestions.add(jsonArray.getJSONArray(i).getString(0))
                }
                return suggestions
            }
        } catch (e: Exception) {
            return emptyList()
        }
        return emptyList()
    }

}