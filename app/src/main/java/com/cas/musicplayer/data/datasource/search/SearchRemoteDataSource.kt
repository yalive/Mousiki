package com.cas.musicplayer.data.datasource.search

import com.cas.common.result.NO_RESULT
import com.cas.common.result.Result
import com.cas.musicplayer.data.preferences.PreferencesHelper
import com.cas.musicplayer.data.remote.mappers.*
import com.cas.musicplayer.data.remote.models.tracks
import com.cas.musicplayer.data.remote.retrofit.MousikiSearchApi
import com.cas.musicplayer.data.remote.retrofit.RetrofitRunner
import com.cas.musicplayer.data.remote.retrofit.YoutubeService
import com.cas.musicplayer.domain.model.Channel
import com.cas.musicplayer.domain.model.MusicTrack
import com.cas.musicplayer.domain.model.Playlist
import com.google.firebase.analytics.FirebaseAnalytics
import javax.inject.Inject

/**
 ***************************************
 * Created by Abdelhadi on 2019-12-09.
 ***************************************
 */
class SearchRemoteDataSource @Inject constructor(
    private var youtubeService: YoutubeService,
    private var mousikiSearchApi: MousikiSearchApi,
    private val retrofitRunner: RetrofitRunner,
    private val trackMapper: YTBVideoToTrack,
    private val playlistMapper: YTBPlaylistToPlaylist,
    private val channelMapper: YTBChannelToChannel,
    private val videoIdMapper: YTBSearchResultToVideoId,
    private val channelIdMapper: YTBSearchResultToChannelId,
    private val playlistIdMapper: YTBSearchResultToPlaylistId,
    private val preferences: PreferencesHelper,
    private val analytics: FirebaseAnalytics
) {

    suspend fun searchTracks(query: String): Result<List<MusicTrack>> {
        // First API
        analytics.logEvent(EVENT_START_SEARCH, null)
        val resultSearch1 = retrofitRunner.executeNetworkCall {
            mousikiSearchApi.search(URL1, query).tracks()
        }
        if (resultSearch1 is Result.Success && resultSearch1.data.isNotEmpty()) {
            return resultSearch1
        }

        // Second API
        val resultSearch2 = retrofitRunner.executeNetworkCall {
            mousikiSearchApi.search(URL2, query).tracks()
        }
        if (resultSearch2 is Result.Success && resultSearch2.data.isNotEmpty()) {
            return resultSearch2
        }

        // Youtube search
        analytics.logEvent(EVENT_SCRAP_NOT_WORKING, null)
        val idsResult = retrofitRunner.executeNetworkCall(videoIdMapper.toListMapper()) {
            youtubeService.searchVideoIdsByQuery(query, 50).items ?: emptyList()
        } as? Result.Success ?: return NO_RESULT

        // 2 - Get videos
        val ids = idsResult.data.joinToString { it.id }
        return retrofitRunner.executeNetworkCall(trackMapper.toListMapper()) {
            youtubeService.videos(ids).items ?: emptyList()
        }
    }

    suspend fun searchPlaylists(query: String): Result<List<Playlist>> {
        val idsResult = retrofitRunner.executeNetworkCall(playlistIdMapper.toListMapper()) {
            youtubeService.searchItemIdsByQuery(query, "playlist", 30).items!!
        } as? Result.Success ?: return NO_RESULT

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
        } as? Result.Success ?: return NO_RESULT

        val ids = idsResult.data.joinToString { it.id }
        return retrofitRunner.executeNetworkCall(channelMapper.toListMapper()) {
            youtubeService.channels(ids).items ?: emptyList()
        }
    }

    companion object {
        private const val URL1 = "https://youtube-scrape.herokuapp.com/api/search"
        private const val URL2 = "https://mousikiapp.herokuapp.com/api/search"
    }
}

private val EVENT_SCRAP_NOT_WORKING = "search_by_scrap_down"
private val EVENT_START_SEARCH = "start_search_by_query"