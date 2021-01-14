package com.cas.musicplayer.data.datasource.search

import com.cas.common.result.NO_RESULT
import com.cas.common.result.Result
import com.cas.common.result.map
import com.cas.musicplayer.data.config.RemoteAppConfig
import com.cas.musicplayer.data.preferences.PreferencesHelper
import com.cas.musicplayer.data.remote.mappers.*
import com.cas.musicplayer.data.remote.models.mousiki.searchResults
import com.cas.musicplayer.data.remote.retrofit.MousikiSearchApi
import com.cas.musicplayer.data.remote.retrofit.RetrofitRunner
import com.cas.musicplayer.data.remote.retrofit.YoutubeService
import com.cas.musicplayer.domain.model.Channel
import com.cas.musicplayer.domain.model.Playlist
import com.cas.musicplayer.domain.model.SearchTracksResult
import com.cas.musicplayer.domain.model.hasData
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
    private val analytics: FirebaseAnalytics,
    private val remoteConfig: RemoteAppConfig
) {

    suspend fun searchTracks(
        query: String,
        key: String? = null,
        token: String? = null
    ): Result<SearchTracksResult> {
        // First API
        analytics.logEvent(EVENT_START_SEARCH, null)

        val resultSearch = retrofitRunner.getMusicTracksWithPagination(
            config = remoteConfig.searchConfig(),
            requestWithApi = { apiUrl ->
                mousikiSearchApi.search(apiUrl, query, key, token).searchResults()
            }
        )
        if (resultSearch.hasData()) return resultSearch

        // Youtube search
        analytics.logEvent(EVENT_SCRAP_NOT_WORKING, null)
        val idsResult = retrofitRunner.executeNetworkCall(videoIdMapper.toListMapper()) {
            youtubeService.searchVideoIdsByQuery(query, 50).items ?: emptyList()
        } as? Result.Success ?: return NO_RESULT

        // 2 - Get videos
        val ids = idsResult.data.joinToString { it.id }
        val executeNetworkCall = retrofitRunner.executeNetworkCall(trackMapper.toListMapper()) {
            youtubeService.videos(ids).items ?: emptyList()
        }
        return executeNetworkCall.map { SearchTracksResult(it) }
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
}

private val EVENT_SCRAP_NOT_WORKING = "search_by_scrap_down"
private val EVENT_START_SEARCH = "start_search_by_query"

fun <T> Result<List<T>>.hasData(): Boolean {
    return (this is Result.Success && data.isNotEmpty())
}

fun List<String>.getOrEmpty(index: Int): String {
    return getOrElse(index) { "" }
}