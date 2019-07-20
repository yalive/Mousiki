package com.cas.musicplayer.repository

import com.cas.musicplayer.base.common.Resource
import com.cas.musicplayer.data.enteties.MusicTrack
import com.cas.musicplayer.data.enteties.Playlist
import com.cas.musicplayer.data.mappers.YTBPlaylistItemToVideoId
import com.cas.musicplayer.data.mappers.YTBPlaylistToPlaylist
import com.cas.musicplayer.data.mappers.YTBVideoToTrack
import com.cas.musicplayer.data.mappers.toListMapper
import com.cas.musicplayer.net.RetrofitRunner
import com.cas.musicplayer.net.Success
import com.cas.musicplayer.net.YoutubeService
import com.cas.musicplayer.net.toResource
import javax.inject.Inject
import javax.inject.Singleton

/**
 ***************************************
 * Created by Abdelhadi on 2019-06-10.
 ***************************************
 */
@Singleton
class GenresRepository @Inject constructor(
    private var youtubeService: YoutubeService,
    private val retrofitRunner: RetrofitRunner,
    private val trackMapper: YTBVideoToTrack,
    private val videoIdMapper: YTBPlaylistItemToVideoId,
    private val playlistMapper: YTBPlaylistToPlaylist
) {

    suspend fun getTopTracks(topTracksPlaylistId: String): Resource<List<MusicTrack>> {
        // 1 - Get video ids
        val idsResult = retrofitRunner.executeNetworkCall(videoIdMapper.toListMapper()) {
            youtubeService.playlistVideoIds(topTracksPlaylistId, 50).items!!
        } as? Success ?: return Resource.error("")

        // 2 - Get videos
        val ids = idsResult.data.joinToString { it.id }
        val videosResult = retrofitRunner.executeNetworkCall(trackMapper.toListMapper()) {
            youtubeService.videos(ids).items!!
        }
        return videosResult.toResource()
    }

    suspend fun getPlaylists(channelId: String): Resource<List<Playlist>> {
        return retrofitRunner.executeNetworkCall(playlistMapper.toListMapper()) {
            youtubeService.channelPlaylists(channelId).items!!
        }.toResource()
    }
}

