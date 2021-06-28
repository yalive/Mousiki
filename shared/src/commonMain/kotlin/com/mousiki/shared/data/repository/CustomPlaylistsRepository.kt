package com.mousiki.shared.data.repository

import com.cas.musicplayer.MousikiDb
import com.mousiki.shared.data.db.CustomPlaylistTrackEntity
import com.mousiki.shared.data.db.imgUrl
import com.mousiki.shared.domain.models.YtbTrack
import com.mousiki.shared.domain.models.Playlist

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
                it.value[0].youtube_id.isNotEmpty() -> it.value[0].imgUrl
                it.value.size > 1 -> it.value[1].imgUrl
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

    suspend fun getCustomPlaylistTracks(playlistName: String): List<YtbTrack> {
        val allTracks = customPlaylistTrackDao.getAll().executeAsList()
        return allTracks.filter {
            it.playlist_name == playlistName && it.youtube_id.isNotEmpty()
        }.map {
            YtbTrack(
                youtubeId = it.youtube_id,
                title = it.title,
                duration = it.duration
            )
        }
    }

    suspend fun addMusicTrackToCustomPlaylist(track: YtbTrack, playlistName: String) {
        customPlaylistTrackDao.insert(
            CustomPlaylistTrackEntity(
                id = 0,
                youtube_id = track.youtubeId,
                duration = track.duration,
                title = track.title,
                playlist_name = playlistName
            )
        )
    }

    suspend fun deleteTrackFromCustomPlaylist(track: YtbTrack, playlistName: String) {
        customPlaylistTrackDao.deleteTrackFromPlaylist(
            youtube_id = track.youtubeId,
            playlist_name = playlistName
        )
    }

    suspend fun deleteCustomPlaylist(playlistName: String) {
        customPlaylistTrackDao.clearCustomPlaylist(playlistName)
    }
}