package com.cas.musicplayer.data.datasource.search

import com.cas.common.result.NO_RESULT
import com.cas.common.result.Result
import com.cas.musicplayer.data.preferences.PreferencesHelper
import com.cas.musicplayer.data.remote.mappers.*
import com.cas.musicplayer.data.remote.retrofit.RetrofitRunner
import com.cas.musicplayer.data.remote.retrofit.YoutubeService
import com.cas.musicplayer.domain.model.Channel
import com.cas.musicplayer.domain.model.MusicTrack
import com.cas.musicplayer.domain.model.Playlist
import javax.inject.Inject

/**
 ***************************************
 * Created by Abdelhadi on 2019-12-09.
 ***************************************
 */
class SearchRemoteDataSource @Inject constructor(
    private var youtubeService: YoutubeService,
    private val retrofitRunner: RetrofitRunner,
    private val trackMapper: YTBVideoToTrack,
    private val playlistMapper: YTBPlaylistToPlaylist,
    private val channelMapper: YTBChannelToChannel,
    private val videoIdMapper: YTBSearchResultToVideoId,
    private val channelIdMapper: YTBSearchResultToChannelId,
    private val playlistIdMapper: YTBSearchResultToPlaylistId,
    private val preferences: PreferencesHelper
) {

    suspend fun searchTracks(query: String): Result<List<MusicTrack>> {
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
}