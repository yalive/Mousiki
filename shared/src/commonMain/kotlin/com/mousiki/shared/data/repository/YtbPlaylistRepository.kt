package com.mousiki.shared.data.repository

import com.mousiki.shared.data.datasource.playlist.PlaylistSongsLocalDataSource
import com.mousiki.shared.data.datasource.playlist.PlaylistSongsRemoteDataSource
import com.mousiki.shared.domain.models.YtbTrack
import com.mousiki.shared.domain.result.Result
import com.mousiki.shared.domain.result.Result.Success
import com.mousiki.shared.domain.result.alsoWhenSuccess

/**
 ***************************************
 * Created by Abdelhadi on 2019-06-12.
 ***************************************
 */

class YtbPlaylistRepository(
    private val playlistSongsLocalDataSource: PlaylistSongsLocalDataSource,
    private val playlistSongsRemoteDataSource: PlaylistSongsRemoteDataSource
) {

    suspend fun playlistVideos(playlistId: String): Result<List<YtbTrack>> {
        val localPlaylists = playlistSongsLocalDataSource.getPlaylistSongs(playlistId)
        if (localPlaylists.isNotEmpty()) {
            return Success(localPlaylists)
        }
        return playlistSongsRemoteDataSource.getPlaylistSongs(playlistId).alsoWhenSuccess {
            playlistSongsLocalDataSource.savePlaylistSongs(playlistId, it)
        }
    }
}