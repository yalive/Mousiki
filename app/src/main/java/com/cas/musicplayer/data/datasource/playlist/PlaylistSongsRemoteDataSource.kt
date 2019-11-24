package com.cas.musicplayer.data.datasource.playlist

import com.cas.common.result.NO_RESULT
import com.cas.common.result.Result
import com.cas.musicplayer.data.remote.mappers.YTBPlaylistItemToVideoId
import com.cas.musicplayer.data.remote.mappers.YTBVideoToTrack
import com.cas.musicplayer.data.remote.mappers.toListMapper
import com.cas.musicplayer.data.remote.retrofit.RetrofitRunner
import com.cas.musicplayer.data.remote.retrofit.YoutubeService
import com.cas.musicplayer.domain.model.MusicTrack
import javax.inject.Inject

/**
 ***************************************
 * Created by Abdelhadi on 2019-11-24.
 ***************************************
 */
class PlaylistSongsRemoteDataSource @Inject constructor(
    private var youtubeService: YoutubeService,
    private val retrofitRunner: RetrofitRunner,
    private val videoIdMapper: YTBPlaylistItemToVideoId,
    private val trackMapper: YTBVideoToTrack
) {

    suspend fun getPlaylistSongs(playlistId: String): Result<List<MusicTrack>> {
        // 1 - Get video ids
        val idsResult = retrofitRunner.executeNetworkCall(videoIdMapper.toListMapper()) {
            youtubeService.playlistVideoIds(playlistId, 50).items ?: emptyList()
        } as? Result.Success ?: return NO_RESULT

        // 2 - Get videos
        val ids = idsResult.data.joinToString { it.id }
        return retrofitRunner.executeNetworkCall(trackMapper.toListMapper()) {
            youtubeService.videos(ids).items!!
        }
    }
}