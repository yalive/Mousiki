package com.cas.musicplayer.data.datasource.channel

import com.mousiki.shared.domain.result.Result
import com.cas.musicplayer.data.remote.mappers.YTBPlaylistToPlaylist
import com.cas.musicplayer.data.remote.mappers.toListMapper
import com.cas.musicplayer.data.remote.retrofit.RetrofitRunner
import com.cas.musicplayer.data.remote.retrofit.YoutubeService
import com.mousiki.shared.domain.models.Playlist
import javax.inject.Inject

/**
 ***************************************
 * Created by Abdelhadi on 2019-11-24.
 ***************************************
 */
class ChannelPlaylistsRemoteDataSource @Inject constructor(
    private var youtubeService: YoutubeService,
    private val retrofitRunner: RetrofitRunner,
    private val playlistMapper: YTBPlaylistToPlaylist
) {

    suspend fun getChannelPlaylists(channelId: String): Result<List<Playlist>> {
        return retrofitRunner.executeNetworkCall(playlistMapper.toListMapper()) {
            youtubeService.channelPlaylists(channelId).items ?: emptyList()
        }
    }
}