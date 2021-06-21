package com.mousiki.shared.data.datasource.search

import com.mousiki.shared.data.config.RemoteAppConfig
import com.mousiki.shared.data.models.searchResults
import com.mousiki.shared.data.remote.api.MousikiApi
import com.mousiki.shared.data.remote.mapper.*
import com.mousiki.shared.data.remote.runner.NetworkRunner
import com.mousiki.shared.domain.models.Channel
import com.mousiki.shared.domain.models.Playlist
import com.mousiki.shared.domain.models.SearchTracksResult
import com.mousiki.shared.domain.models.hasData
import com.mousiki.shared.domain.result.NO_RESULT
import com.mousiki.shared.domain.result.Result
import com.mousiki.shared.domain.result.map
import com.mousiki.shared.utils.AnalyticsApi

/**
 ***************************************
 * Created by Abdelhadi on 2019-12-09.
 ***************************************
 */
class SearchRemoteDataSource(
    private val networkRunner: NetworkRunner,
    private val trackMapper: YTBVideoToTrack,
    private val playlistMapper: YTBPlaylistToPlaylist,
    private val channelMapper: YTBChannelToChannel,
    private val videoIdMapper: YTBSearchResultToVideoId,
    private val channelIdMapper: YTBSearchResultToChannelId,
    private val playlistIdMapper: YTBSearchResultToPlaylistId,
    private val analytics: AnalyticsApi,
    private val remoteConfig: RemoteAppConfig,
    private val mousikiApi: MousikiApi
) {

    suspend fun searchTracks(
        query: String,
        key: String? = null,
        token: String? = null
    ): Result<SearchTracksResult> {
        // First API
        analytics.logEvent(EVENT_START_SEARCH)

        val resultSearch = networkRunner.loadWithRetry(remoteConfig.searchConfig()) { apiUrl ->
            mousikiApi.search(apiUrl, query, key, token).searchResults()
        }
        if (resultSearch.hasData()) return resultSearch

        // Youtube search
        analytics.logEvent(EVENT_SCRAP_NOT_WORKING)
        val idsResult = networkRunner.executeNetworkCall(videoIdMapper.toListMapper()) {
            mousikiApi.searchVideoIdsByQuery(query, 50).items ?: emptyList()
        } as? Result.Success ?: return NO_RESULT

        // 2 - Get videos
        val ids = idsResult.data.joinToString { it.id }
        val executeNetworkCall = networkRunner.executeNetworkCall(trackMapper.toListMapper()) {
            mousikiApi.videos(ids).items ?: emptyList()
        }
        return executeNetworkCall.map { SearchTracksResult(it) }
    }

    suspend fun searchPlaylists(query: String): Result<List<Playlist>> {
        val idsResult = networkRunner.executeNetworkCall(playlistIdMapper.toListMapper()) {
            mousikiApi.searchItemIdsByQuery(query, "playlist", 30).items!!
        } as? Result.Success ?: return NO_RESULT

        // 2 - Get videos
        val ids = idsResult.data.joinToString { it.id }
        val videosResult = networkRunner.executeNetworkCall(playlistMapper.toListMapper()) {
            mousikiApi.playlists(ids).items!!
        }
        return videosResult
    }


    suspend fun searchChannels(query: String): Result<List<Channel>> {
        val idsResult = networkRunner.executeNetworkCall(channelIdMapper.toListMapper()) {
            mousikiApi.searchItemIdsByQuery(query, "channel", 15).items ?: emptyList()
        } as? Result.Success ?: return NO_RESULT

        val ids = idsResult.data.joinToString { it.id }
        return networkRunner.executeNetworkCall(channelMapper.toListMapper()) {
            mousikiApi.channels(ids).items ?: emptyList()
        }
    }
}

private val EVENT_SCRAP_NOT_WORKING = "search_by_scrap_down"
private val EVENT_START_SEARCH = "start_search_by_query"
