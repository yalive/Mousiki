package com.cas.musicplayer.data.repositories

import com.cas.musicplayer.data.local.database.dao.CustomPlaylistTrackDao
import com.cas.musicplayer.data.local.models.CustomPlaylistEntity
import com.cas.musicplayer.data.local.models.imgUrl
import com.mousiki.shared.domain.models.MusicTrack
import com.mousiki.shared.domain.models.Playlist
import javax.inject.Inject
import javax.inject.Singleton

/**
 ***************************************
 * Created by Y.Abdelhadi on 4/4/20.
 ***************************************
 */
@Singleton
class CustomPlaylistsRepository @Inject constructor(
    private val customPlaylistTrackDao: CustomPlaylistTrackDao
) {

    suspend fun getCustomPlaylists(): List<Playlist> {
        val allTracks = customPlaylistTrackDao.getAll()
        val groupedTracks = allTracks.groupBy { it.playlistName }
        return groupedTracks.map {
            val urlImage = when {
                it.value[0].youtubeId.isNotEmpty() -> it.value[0].imgUrl
                it.value.size > 1 -> it.value[1].imgUrl
                else -> ""
            }
            Playlist(
                id = it.key, // Label as id!!
                itemCount = it.value.count { it.youtubeId.isNotEmpty() },
                title = it.key,
                urlImage = urlImage
            )
        }
    }

    suspend fun getCustomPlaylistTracks(playlistName: String): List<MusicTrack> {
        val allTracks = customPlaylistTrackDao.getAll()
        return allTracks.filter {
            it.playlistName == playlistName && it.youtubeId.isNotEmpty()
        }.map {
            MusicTrack(
                youtubeId = it.youtubeId,
                title = it.title,
                duration = it.duration
            )
        }
    }

    suspend fun addMusicTrackToCustomPlaylist(track: MusicTrack, playlistName: String) {
        customPlaylistTrackDao.insertMusicTrack(
            CustomPlaylistEntity(
                youtubeId = track.youtubeId,
                duration = track.duration,
                title = track.title,
                playlistName = playlistName
            )
        )
    }

    suspend fun deleteTrackFromCustomPlaylist(track: MusicTrack, playlistName: String) {
        customPlaylistTrackDao.deleteTrackFromPlaylist(
            trackId = track.youtubeId,
            playlistName = playlistName
        )
    }

    suspend fun deleteCustomPlaylist(playlistName: String) {
        customPlaylistTrackDao.clearCustomPlaylist(playlistName)
    }
}