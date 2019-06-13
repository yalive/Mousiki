package com.secureappinc.musicplayer.repository

import com.secureappinc.musicplayer.data.enteties.MusicTrack
import com.secureappinc.musicplayer.data.enteties.Playlist
import com.secureappinc.musicplayer.data.mappers.YTBPlaylistItemToVideoId
import com.secureappinc.musicplayer.data.mappers.YTBPlaylistToPlaylist
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
            youtubeService.playlists(channelId, "ma").items!!
        }.toResource()
    }
}

