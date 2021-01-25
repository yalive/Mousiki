package com.cas.musicplayer.data.local.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.cas.musicplayer.data.local.models.SearchQueryEntity

/**
 ***************************************
 * Created by Abdelhadi on 2019-12-09.
 ***************************************
 */
@Dao
interface SearchQueryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(artist: SearchQueryEntity)

    @Query("SELECT * from search_queries")
    suspend fun getAll(): List<SearchQueryEntity>

    @Query("SELECT * FROM search_queries WHERE `query` LIKE :keyword")
    suspend fun search(keyword: String): List<SearchQueryEntity>
}