package com.cas.musicplayer.repository

import com.cas.musicplayer.data.enteties.MusicTrack
import com.cas.musicplayer.data.enteties.Playlist
import com.cas.musicplayer.data.mappers.*
import com.cas.musicplayer.net.NO_RESULT
import com.cas.musicplayer.net.Result
import com.cas.musicplayer.net.Result.Success
import com.cas.musicplayer.net.RetrofitRunner
import com.cas.musicplayer.net.YoutubeService
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
    private val videoIdMapper: YTBPlaylistItemToVideoId,
    private val playlistMapper: YTBPlaylistToPlaylist
) {

    suspend fun playlistVideos(playlistId: String): Result<List<MusicTrack>> {
        // 1 - Get video ids
        val idsResult = retrofitRunner.executeNetworkCall(videoIdMapper.toListMapper()) {
            youtubeService.playlistVideoIds(playlistId, 50).items ?: emptyList()
        } as? Success ?: return NO_RESULT

        // 2 - Get videos
        val ids = idsResult.data.joinToString { it.id }
        return retrofitRunner.executeNetworkCall(trackMapper.toListMapper()) {
            youtubeService.videos(ids).items!!
        }
    }

    suspend fun firstThreeVideo(playlistId: String): Result<List<MusicTrack>> {
        return retrofitRunner.executeNetworkCall(playlistTrackMapper.toListMapper()) {
            youtubeService.playlistVideosTitle(playlistId, 3).items ?: emptyList()
        }
    }

    suspend fun getPlaylists(channelId: String): Result<List<Playlist>> {
        return retrofitRunner.executeNetworkCall(playlistMapper.toListMapper()) {
            youtubeService.channelPlaylists(channelId).items ?: emptyList()
        }
    }
}