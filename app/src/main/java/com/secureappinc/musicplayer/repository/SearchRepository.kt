package com.secureappinc.musicplayer.repository

import com.secureappinc.musicplayer.base.common.Resource
import com.secureappinc.musicplayer.data.enteties.Channel
import com.secureappinc.musicplayer.data.enteties.MusicTrack
import com.secureappinc.musicplayer.data.enteties.Playlist
import com.secureappinc.musicplayer.data.mappers.*
import com.secureappinc.musicplayer.net.RetrofitRunner
import com.secureappinc.musicplayer.net.Success
import com.secureappinc.musicplayer.net.YoutubeService
import com.secureappinc.musicplayer.net.toResource
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

    suspend fun searchTracks(query: String): Resource<List<MusicTrack>> {
        val idsResult = retrofitRunner.executeNetworkCall(videoIdMapper.toListMapper()) {
            youtubeService.searchVideoIdsByQuery(query, 50).items!!
        } as? Success ?: return Resource.error("")

        // 2 - Get videos
        val ids = idsResult.data.joinToString { it.id }
        val videosResult = retrofitRunner.executeNetworkCall(trackMapper.toListMapper()) {
            youtubeService.videos(ids).items!!
        }
        return videosResult.toResource()
    }

    suspend fun searchPlaylists(query: String): Resource<List<Playlist>> {
        val idsResult = retrofitRunner.executeNetworkCall(playlistIdMapper.toListMapper()) {
            youtubeService.searchItemIdsByQuery(query, "playlist", 30).items!!
        } as? Success ?: return Resource.error("")

        // 2 - Get videos
        val ids = idsResult.data.joinToString { it.id }
        val videosResult = retrofitRunner.executeNetworkCall(playlistMapper.toListMapper()) {
            youtubeService.playlists(ids).items!!
        }
        return videosResult.toResource()
    }


    suspend fun searchChannels(query: String): Resource<List<Channel>> {
        val idsResult = retrofitRunner.executeNetworkCall(channelIdMapper.toListMapper()) {
            youtubeService.searchItemIdsByQuery(query, "channel", 15).items!!
        } as? Success ?: return Resource.error("")

        val ids = idsResult.data.joinToString { it.id }
        val videosResult = retrofitRunner.executeNetworkCall(channelMapper.toListMapper()) {
            youtubeService.channels(ids).items!!
        }
        return videosResult.toResource()
    }

    suspend fun getSuggestions(url: String): List<String> {
        try {
            val responseBody = youtubeService.suggestions(url)
            val stringResponse = responseBody.string()
            if (stringResponse.startsWith("window.google.ac.h")) {
                val json =
                    stringResponse.substring(stringResponse.indexOf("(") + 1, stringResponse.indexOf(")"))

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