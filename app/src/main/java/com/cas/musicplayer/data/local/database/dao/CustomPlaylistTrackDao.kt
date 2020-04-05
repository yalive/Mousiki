package com.cas.musicplayer.data.local.database.dao

import androidx.room.*
import com.cas.musicplayer.data.local.models.CustomPlaylistEntity

/**
 ***************************************
 * Created by Y.Abdelhadi on 4/4/20.
 ***************************************
 */

@Dao
interface CustomPlaylistTrackDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMusicTrack(musicTrack: CustomPlaylistEntity)

    @Query("SELECT * from custom_playlist_track")
    suspend fun getAll(): List<CustomPlaylistEntity>

    @Delete
    suspend fun deleteTrackFromPlaylist(musicTrack: CustomPlaylistEntity)

    @Query("DELETE  from custom_playlist_track WHERE playlist_name=:playlistName")
    suspend fun clearCustomPlaylist(playlistName: String)
}