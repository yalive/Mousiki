package com.cas.musicplayer.data.local.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.cas.musicplayer.data.local.models.LightSongEntity

/**
 ***************************************
 * Created by Abdelhadi on 2019-11-24.
 ***************************************
 */
@Dao
interface LightSongDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(song: LightSongEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(songs: List<LightSongEntity>)

    @Query("SELECT * from playlist_first_three_tracks WHERE playlistId=:playlistId")
    suspend fun getPlaylistSongs(playlistId: String): List<LightSongEntity>

    @Query("DELETE  from playlist_first_three_tracks")
    suspend fun clear()

    @Query("SELECT COUNT(*) from playlist_first_three_tracks")
    suspend fun count(): Int
}
