package com.cas.musicplayer.data.local.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.cas.musicplayer.data.local.models.SearchPlaylistEntity

/**
 ***************************************
 * Created by Abdelhadi on 2019-12-09.
 ***************************************
 */

@Dao
interface SearchPlaylistsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(playlists: List<SearchPlaylistEntity>)

    @Query("SELECT * FROM playlists_search_result WHERE `query`=:query")
    suspend fun getResultForQuery(query: String): List<SearchPlaylistEntity>
}