package com.mousiki.shared.data.datasource.playlist

import com.cas.musicplayer.MousikiDb
import com.mousiki.shared.data.db.PlaylistTrackEntity
import com.mousiki.shared.data.db.toMusicTrack
import com.mousiki.shared.domain.models.YtbTrack
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 ***************************************
 * Created by Abdelhadi on 2019-11-24.
 ***************************************
 */

class PlaylistSongsLocalDataSource(
    private val db: MousikiDb
) {

    private val playlistSongsDao by lazy { db.playlistTracksQueries }

    suspend fun getPlaylistSongs(playlistId: String): List<YtbTrack> =
        withContext(Dispatchers.Default) {
            return@withContext playlistSongsDao.getPlaylistTracks(playlistId).executeAsList().map {
                it.toMusicTrack()
            }
        }

    suspend fun savePlaylistSongs(playlistId: String, tracks: List<YtbTrack>) =
        withContext(Dispatchers.Default) {
            val channelSongs = tracks.map {
                PlaylistTrackEntity(
                    id = 0,
                    youtube_id = it.youtubeId,
                    playlistId = playlistId,
                    title = it.title,
                    duration = it.duration
                )
            }

            db.transaction {
                channelSongs.forEach { song ->
                    playlistSongsDao.insert(song)
                }
            }
        }
}