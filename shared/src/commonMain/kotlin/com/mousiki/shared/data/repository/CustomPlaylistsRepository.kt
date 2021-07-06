package com.mousiki.shared.data.repository

import com.cas.musicplayer.MousikiDb
import com.mousiki.shared.data.db.CustomPlaylistTrackEntity
import com.mousiki.shared.data.db.toTrack
import com.mousiki.shared.db.Custom_playlist_track
import com.mousiki.shared.domain.models.Playlist
import com.mousiki.shared.domain.models.Track
import com.mousiki.shared.domain.models.imgUrlDef0

/**
 ***************************************
 * Created by Y.Abdelhadi on 4/4/20.
 ***************************************
 */
class CustomPlaylistsRepository(
    private val db: MousikiDb,
) {
    private val customPlaylistTrackDao by lazy { db.customPlaylistTrackQueries }
    suspend fun getCustomPlaylists(): List<Playlist> {
        val allTracks = customPlaylistTrackDao.getAll().executeAsList()
        val groupedTracks = allTracks.groupBy { it.playlist_name }
        return groupedTracks.map {

            val urlImage = when {
                it.value[0].youtube_id.isNotEmpty() -> it.value[0].toTrack().imgUrlDef0
                it.value.size > 1 -> it.value[1].toTrack().imgUrlDef0
                else -> ""
            }
            Playlist(
                id = it.key, // Label as id!!
                itemCount = it.value.count { it.youtube_id.isNotEmpty() },
                title = it.key,
                urlImage = urlImage
            )
        }
    }

    suspend fun getCustomPlaylistTracks(playlistName: String): List<Track> {
        val allTracks = customPlaylistTrackDao.getAll().executeAsList()
        return allTracks.filter {
            it.playlist_name == playlistName && it.youtube_id.isNotEmpty()
        }.map(Custom_playlist_track::toTrack)
    }

    suspend fun addMusicTrackToCustomPlaylist(track: Track, playlistName: String) {
        customPlaylistTrackDao.insert(
            CustomPlaylistTrackEntity(
                id = 0,
                youtube_id = track.id,
                duration = track.duration,
                title = track.title,
                playlist_name = playlistName
            )
        )
    }

    suspend fun deleteTrackFromCustomPlaylist(track: Track, playlistName: String) {
        customPlaylistTrackDao.deleteTrackFromPlaylist(
            youtube_id = track.id,
            playlist_name = playlistName
        )
    }

    suspend fun deleteCustomPlaylist(playlistName: String) {
        customPlaylistTrackDao.clearCustomPlaylist(playlistName)
    }
}