package com.mousiki.shared.data.datasource.channel

import com.mousiki.shared.data.remote.api.MousikiApi
import com.mousiki.shared.data.remote.mapper.YTBPlaylistToPlaylist
import com.mousiki.shared.data.remote.mapper.toListMapper
import com.mousiki.shared.data.remote.runner.NetworkRunner
import com.mousiki.shared.domain.models.Playlist
import com.mousiki.shared.domain.result.Result

/**
 ***************************************
 * Created by Abdelhadi on 2019-11-24.
 ***************************************
 */
class ChannelPlaylistsRemoteDataSource(
    private var mousikiApi: MousikiApi,
    private val networkRunner: NetworkRunner,
    private val playlistMapper: YTBPlaylistToPlaylist
) {

    suspend fun getChannelPlaylists(channelId: String): Result<List<Playlist>> {
        return networkRunner.executeNetworkCall(playlistMapper.toListMapper()) {
            mousikiApi.channelPlaylists(channelId).items ?: emptyList()
        }
    }
}