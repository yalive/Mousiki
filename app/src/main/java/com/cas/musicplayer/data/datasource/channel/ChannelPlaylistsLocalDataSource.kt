package com.cas.musicplayer.data.datasource.channel

import com.cas.musicplayer.data.local.database.dao.ChannelPlaylistsDao
import com.cas.musicplayer.data.local.models.ChannelPlaylistEntity
import com.cas.musicplayer.data.local.models.toPlaylist
import com.cas.musicplayer.domain.model.Playlist
import com.cas.musicplayer.utils.bgContext
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 ***************************************
 * Created by Abdelhadi on 2019-11-24.
 ***************************************
 */
class ChannelPlaylistsLocalDataSource @Inject constructor(
    private val channelPlaylistsDao: ChannelPlaylistsDao
) {

    suspend fun getChannelPlaylists(channelId: String): List<Playlist> = withContext(bgContext) {
        return@withContext channelPlaylistsDao.getChannelPlaylists(channelId).map {
            it.toPlaylist()
        }
    }

    suspend fun saveChannelPlaylists(channelId: String, playlists: List<Playlist>) =
        withContext(bgContext) {
            val channelPlaylists = playlists.map {
                ChannelPlaylistEntity(
                    playlistId = it.id,
                    channelId = channelId,
                    title = it.title,
                    urlImage = it.urlImage,
                    itemCount = it.itemCount
                )
            }
            channelPlaylistsDao.insert(channelPlaylists)
        }
}