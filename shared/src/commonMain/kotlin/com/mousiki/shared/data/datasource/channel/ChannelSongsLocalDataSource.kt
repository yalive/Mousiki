package com.mousiki.shared.data.datasource.channel

import com.cas.musicplayer.MousikiDb
import com.mousiki.shared.data.db.toMusicTrack
import com.mousiki.shared.db.Channel_tracks
import com.mousiki.shared.domain.models.MusicTrack
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

    suspend fun getChannelSongs(channelId: String): List<MusicTrack> =
        withContext(Dispatchers.Default) {
            return@withContext channelSongsDao.getChannelTracks(channelId).executeAsList().map {
                it.toMusicTrack()
            }
        }

    suspend fun saveChannelSongs(channelId: String, tracks: List<MusicTrack>) =
        withContext(Dispatchers.Default) {
            val channelSongs = tracks.map {
                Channel_tracks(
                    id = 0,
                    youtube_id = it.youtubeId,
                    channelId = channelId,
                    title = it.title,
                    duration = it.duration
                )
            }

            db.transaction {
                channelSongs.forEach { song ->
                    channelSongsDao.insert(song)
                }
            }
        }
}