package com.cas.musicplayer.data.local.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.cas.musicplayer.data.local.models.SearchSongEntity

/**
 ***************************************
 * Created by Abdelhadi on 2019-12-09.
 ***************************************
 */

@Dao
interface SearchSongDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertTrack(musicTrack: SearchSongEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(songs: List<SearchSongEntity>)

    @Query("SELECT * FROM songs_search_result WHERE `query`=:query")
    suspend fun getResultForQuery(query: String): List<SearchSongEntity>
}