package com.cas.musicplayer.data.repositories

import com.mousiki.shared.data.datasource.channel.ChannelPlaylistsRemoteDataSource
import com.cas.musicplayer.data.datasource.playlist.PlaylistSongsRemoteDataSource
import com.mousiki.shared.data.datasource.channel.ChannelPlaylistsLocalDataSource
import com.mousiki.shared.data.datasource.playlist.PlaylistSongsLocalDataSource
import com.mousiki.shared.domain.models.MusicTrack
import com.mousiki.shared.domain.models.Playlist
import com.mousiki.shared.domain.result.Result
import com.mousiki.shared.domain.result.Result.Success
import com.mousiki.shared.domain.result.alsoWhenSuccess
import javax.inject.Inject
import javax.inject.Singleton

/**
 ***************************************
 * Created by Abdelhadi on 2019-06-12.
 ***************************************
 */

@Singleton
class PlaylistRepository @Inject constructor(
    private val channelPlaylistsLocalDataSource: ChannelPlaylistsLocalDataSource,
    private val channelPlaylistsRemoteDataSource: ChannelPlaylistsRemoteDataSource,
    private val playlistSongsLocalDataSource: PlaylistSongsLocalDataSource,
    private val playlistSongsRemoteDataSource: PlaylistSongsRemoteDataSource
) {

    suspend fun playlistVideos(playlistId: String): Result<List<MusicTrack>> {
        val localPlaylists = playlistSongsLocalDataSource.getPlaylistSongs(playlistId)
        if (localPlaylists.isNotEmpty()) {
            return Success(localPlaylists)
        }
        return playlistSongsRemoteDataSource.getPlaylistSongs(playlistId).alsoWhenSuccess {
            playlistSongsLocalDataSource.savePlaylistSongs(playlistId, it)
        }
    }

    suspend fun getPlaylists(channelId: String): Result<List<Playlist>> {
        val localPlaylists = channelPlaylistsLocalDataSource.getChannelPlaylists(channelId)
        if (localPlaylists.isNotEmpty()) {
            return Success(localPlaylists)
        }
        return channelPlaylistsRemoteDataSource.getChannelPlaylists(channelId).alsoWhenSuccess {
            channelPlaylistsLocalDataSource.saveChannelPlaylists(channelId, it)
        }
    }
}