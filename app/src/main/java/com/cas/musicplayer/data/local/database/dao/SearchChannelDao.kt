package com.cas.musicplayer.data.local.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.cas.musicplayer.data.local.models.SearchChannelEntity

/**
 ***************************************
 * Created by Abdelhadi on 2019-12-09.
 ***************************************
 */

@Dao
interface SearchChannelDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(channels: List<SearchChannelEntity>)

    @Query("SELECT * FROM channel_search_result WHERE `query`=:query")
    suspend fun getResultForQuery(query: String): List<SearchChannelEntity>
}