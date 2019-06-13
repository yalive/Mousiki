package com.secureappinc.musicplayer.repository

import com.secureappinc.musicplayer.data.enteties.MusicTrack
import com.secureappinc.musicplayer.data.mappers.YTBPlaylistItemToTrack
import com.secureappinc.musicplayer.data.mappers.YTBPlaylistItemToVideoId
import com.secureappinc.musicplayer.data.mappers.YTBVideoToTrack
import com.secureappinc.musicplayer.data.mappers.toListMapper
import com.secureappinc.musicplayer.base.common.Resource
import com.secureappinc.musicplayer.net.RetrofitRunner
import com.secureappinc.musicplayer.net.Success
import com.secureappinc.musicplayer.net.YoutubeService
import com.secureappinc.musicplayer.net.toResource
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

    suspend fun playlistVideos(playlistId: String): Resource<List<MusicTrack>> {
        // 1 - Get video ids
        val idsResult = retrofitRunner.executeNetworkCall(videoIdMapper.toListMapper()) {
            youtubeService.playlistVideoIds(playlistId, 50).items!!
        } as? Success ?: return Resource.error("")

        // 2 - Get videos
        val ids = idsResult.data.joinToString { it.id }
        val videosResult = retrofitRunner.executeNetworkCall(trackMapper.toListMapper()) {
            youtubeService.videos(ids).items!!
        }
        return videosResult.toResource()
    }

    suspend fun firstThreeVideo(playlistId: String): Resource<List<MusicTrack>> {
        return retrofitRunner.executeNetworkCall(playlistTrackMapper.toListMapper()) {
            youtubeService.playlistVideoTitles(playlistId, 3).items!!
        }.toResource()
    }
}