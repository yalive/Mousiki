package com.cas.musicplayer.repository

import com.cas.musicplayer.base.common.ResourceOld
import com.cas.musicplayer.data.enteties.MusicTrack
import com.cas.musicplayer.data.mappers.YTBPlaylistItemToTrack
import com.cas.musicplayer.data.mappers.YTBPlaylistItemToVideoId
import com.cas.musicplayer.data.mappers.YTBVideoToTrack
import com.cas.musicplayer.data.mappers.toListMapper
import com.cas.musicplayer.net.Result
import com.cas.musicplayer.net.Result.Success
import com.cas.musicplayer.net.RetrofitRunner
import com.cas.musicplayer.net.YoutubeService
import com.cas.musicplayer.net.asOldResource
import javax.inject.Inject
import javax.inject.Singleton

/**
 ***************************************
 * Created by Abdelhadi on 2019-06-12.
 ***************************************
 */

@Singleton
class PlaylistRepository @Inject constructor(
    private var youtubeService: YoutubeService,
    private val retrofitRunner: RetrofitRunner,
    private val trackMapper: YTBVideoToTrack,
    private val playlistTrackMapper: YTBPlaylistItemToTrack,
    private val videoIdMapper: YTBPlaylistItemToVideoId
) {

    suspend fun playlistVideos(playlistId: String): ResourceOld<List<MusicTrack>> {
        // 1 - Get video ids
        val idsResult = retrofitRunner.executeNetworkCall(videoIdMapper.toListMapper()) {
            youtubeService.playlistVideoIds(playlistId, 50).items!!
        } as? Success ?: return ResourceOld.error("")

        // 2 - Get videos
        val ids = idsResult.data.joinToString { it.id }
        val videosResult = retrofitRunner.executeNetworkCall(trackMapper.toListMapper()) {
            youtubeService.videos(ids).items!!
        }
        return videosResult.asOldResource()
    }

    suspend fun firstThreeVideo(playlistId: String): Result<List<MusicTrack>> {
        return retrofitRunner.executeNetworkCall(playlistTrackMapper.toListMapper()) {
            youtubeService.playlistVideosTitle(playlistId, 3).items!!
        }
    }
}