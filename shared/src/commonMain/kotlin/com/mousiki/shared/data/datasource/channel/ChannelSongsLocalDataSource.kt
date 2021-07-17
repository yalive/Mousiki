package com.mousiki.shared.data.datasource.channel

import com.cas.musicplayer.MousikiDb
import com.mousiki.shared.data.db.ChannelTrackEntity
import com.mousiki.shared.data.db.toTrack
import com.mousiki.shared.domain.models.YtbTrack
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 ***************************************
 * Created by Abdelhadi on 2019-11-24.
 ***************************************
 */

class ChannelSongsLocalDataSource(
    private val db: MousikiDb
) {

    private val channelSongsDao = db.channelTracksQueries

    suspend fun getChannelSongs(channelId: String): List<YtbTrack> =
        withContext(Dispatchers.Default) {
            return@withContext channelSongsDao.getChannelTracks(channelId).executeAsList().map {
                it.toTrack()
            }
        }

    suspend fun saveChannelSongs(channelId: String, tracks: List<YtbTrack>) =
        withContext(Dispatchers.Default) {
            val channelSongs = tracks.map {
                ChannelTrackEntity(
                    id = 0,
                    youtube_id = it.youtubeId,
                    channelId = channelId,
                    title = it.title,
                    duration = it.duration,
                    artist_name = it.artistName
                )
            }

            db.transaction {
                channelSongs.forEach { song ->
                    channelSongsDao.insert(song)
                }
            }
        }
}