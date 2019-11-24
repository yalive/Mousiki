package com.cas.musicplayer.data.local.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.cas.musicplayer.data.local.models.SongTitleEntity

/**
 ***************************************
 * Created by Abdelhadi on 2019-11-24.
 ***************************************
 */
@Dao
interface SongTitleDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(song: SongTitleEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(songs: List<SongTitleEntity>)

    @Query("SELECT * from song_titles WHERE playlistId=:playlistId")
    suspend fun getPlaylistSongs(playlistId: String): List<SongTitleEntity>
}
