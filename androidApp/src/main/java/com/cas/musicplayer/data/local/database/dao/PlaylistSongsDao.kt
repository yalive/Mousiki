package com.cas.musicplayer.data.local.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.cas.musicplayer.data.local.models.PlaylistSongEntity

/**
 ***************************************
 * Created by Abdelhadi on 2019-11-24.
 ***************************************
 */
@Dao
interface PlaylistSongsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(song: PlaylistSongEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(songs: List<PlaylistSongEntity>)

    @Query("SELECT * from playlist_tracks WHERE playlistId=:playlistId")
    suspend fun getPlaylistTracks(playlistId: String): List<PlaylistSongEntity>
}
