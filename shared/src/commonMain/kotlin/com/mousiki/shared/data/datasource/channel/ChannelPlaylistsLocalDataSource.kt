package com.mousiki.shared.data.datasource.channel

import com.cas.musicplayer.MousikiDb
import com.mousiki.shared.data.db.toPlaylist
import com.mousiki.shared.db.Channel_playlist
import com.mousiki.shared.domain.models.Playlist
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 ***************************************
 * Created by Abdelhadi on 2019-11-24.
 ***************************************
 */
class ChannelPlaylistsLocalDataSource(
    private val db: MousikiDb
) {

    private val channelPlaylistsDao by lazy { db.channelPlaylistQueries }

    suspend fun getChannelPlaylists(channelId: String): List<Playlist> = withContext(Dispatchers.Default) {
        return@withContext channelPlaylistsDao.getChannelPlaylists(channelId).executeAsList().map {
            it.toPlaylist()
        }
    }

    suspend fun saveChannelPlaylists(channelId: String, playlists: List<Playlist>) =
        withContext(Dispatchers.Default) {
            val channelPlaylists = playlists.map {
                Channel_playlist(
                    id = 0,
                    playlist_id = it.id,
                    channelId = channelId,
                    title = it.title,
                    urlImage = it.urlImage,
                    itemCount = it.itemCount.toLong()
                )
            }
            db.transaction {
                channelPlaylists.forEach { playlist ->
                    channelPlaylistsDao.insert(playlist)
                }
            }
        }
}