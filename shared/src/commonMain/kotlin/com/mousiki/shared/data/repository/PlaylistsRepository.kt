package com.mousiki.shared.data.repository

import com.cas.musicplayer.MousikiDb
import com.mousiki.shared.data.db.*
import com.mousiki.shared.db.Custom_playlist_track
import com.mousiki.shared.db.Db_playlist
import com.mousiki.shared.domain.models.Playlist
import com.mousiki.shared.domain.models.Track
import com.mousiki.shared.utils.DB_DATE_FORMAT
import com.mousiki.shared.utils.KMPDate
import com.squareup.sqldelight.runtime.coroutines.asFlow
import com.squareup.sqldelight.runtime.coroutines.mapToList
import com.squareup.sqldelight.runtime.coroutines.mapToOne
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

/**
 ***************************************
 * Created by Y.Abdelhadi on 4/4/20.
 ***************************************
 */
class PlaylistsRepository(
    private val db: MousikiDb,
) {
    private val customPlaylistTrackDao by lazy { db.customPlaylistTrackQueries }
    private val playlistsDao by lazy { db.playlistQueries }
    private val favouriteDao by lazy { db.favouriteTracksQueries }
    private val recentDao by lazy { db.recentPlayedTracksQueries }

    suspend fun playlistWithNameExist(name: String): Boolean = withContext(Dispatchers.Default) {
        return@withContext playlistsDao.playlistWithNameExist(name).executeAsOne()
    }

    suspend fun createCustomPlaylist(
        name: String,
        description: String = ""
    ): Playlist = withContext(Dispatchers.Default) {
        val currentDate = KMPDate(DB_DATE_FORMAT).asString()
        val dbPlaylist = Db_playlist(
            id = 0,
            name = name,
            description = description,
            type = Playlist.TYPE_CUSTOM,
            created_by = Playlist.CREATED_BY_USER,
            created_at = currentDate,
            edited_at = currentDate,
            external_id = null
        )
        playlistsDao.insert(dbPlaylist)
        return@withContext playlistsDao.getPlaylistWithName(name).executeAsOne().asPlaylist()
    }

    suspend fun getPlaylists(): List<Playlist> = withContext(Dispatchers.Default) {
        return@withContext playlistsDao.getAll().executeAsList()
            .map {
                val count = when (it.type) {
                    Playlist.TYPE_FAV -> favouriteDao.count().executeAsOne()
                    Playlist.TYPE_RECENT -> recentDao.count().executeAsOne()
                    Playlist.TYPE_HEAVY -> recentDao.heavyCount().executeAsOne()
                    Playlist.TYPE_YTB -> TODO("To be implemented")
                    else -> customPlaylistTrackDao.playlistTracksCount(it.id).executeAsOne()
                }.toInt()
                it.asPlaylist(itemCount = count)
            }
    }

    suspend fun getPlaylistsFlow(): Flow<List<Playlist>> = withContext(Dispatchers.Default) {
        return@withContext playlistsDao.getAll().asFlow()
            .mapToList()
            .map { playlists ->
                playlists.map {
                    val count = when (it.type) {
                        Playlist.TYPE_FAV -> favouriteDao.count().executeAsOne()
                        Playlist.TYPE_RECENT -> recentDao.count().executeAsOne()
                        Playlist.TYPE_HEAVY -> recentDao.heavyCount().executeAsOne()
                        Playlist.TYPE_YTB -> TODO("To be implemented")
                        else -> customPlaylistTrackDao.playlistTracksCount(it.id).executeAsOne()
                    }.toInt()
                    it.asPlaylist(itemCount = count)
                }
            }
    }

    suspend fun getCustomPlaylistTracks(
        playlistId: Long
    ): List<Track> = withContext(Dispatchers.Default) {
        return@withContext customPlaylistTrackDao.getPlaylistTracks(playlistId)
            .executeAsList()
            .map(Custom_playlist_track::toTrack)
    }


    suspend fun addTrackToCustomPlaylist(
        track: Track, playlistId: Long
    ) = withContext(Dispatchers.Default) {
        customPlaylistTrackDao.insert(
            CustomPlaylistTrackEntity(
                id = 0,
                track_id = track.id,
                duration = track.duration,
                title = track.title,
                playlist_id = playlistId
            )
        )
    }

    suspend fun deleteTrackFromCustomPlaylist(
        track: Track, playlistId: Long
    ) = withContext(Dispatchers.Default) {
        customPlaylistTrackDao.deleteTrackFromPlaylist(
            track_id = track.id,
            playlist_id = playlistId
        )
    }

    suspend fun deleteCustomPlaylist(playlistId: Long) = withContext(Dispatchers.Default) {
        customPlaylistTrackDao.clearCustomPlaylistTracks(playlistId)
        playlistsDao.delete(playlistId)
    }

    suspend fun getPlaylistItemsCount(
        playlist: Playlist
    ): Flow<Long> = withContext(Dispatchers.Default) {
        return@withContext when (playlist.type) {
            Playlist.TYPE_FAV -> favouriteDao.count().asFlow().mapToOne()
            Playlist.TYPE_RECENT -> recentDao.count().asFlow().mapToOne()
            Playlist.TYPE_HEAVY -> recentDao.heavyCount().asFlow().mapToOne()
            Playlist.TYPE_YTB -> TODO("To be implemented")
            else -> customPlaylistTrackDao.playlistTracksCount(playlist.id.toLong())
                .asFlow().mapToOne()
        }
    }
}