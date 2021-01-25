package com.cas.musicplayer.data.local.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.cas.musicplayer.data.local.models.ArtistEntity

/**
 ***************************************
 * Created by Abdelhadi on 2019-11-24.
 ***************************************
 */
@Dao
interface ArtistDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(artist: ArtistEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(artists: List<ArtistEntity>)

    @Query("SELECT * from artists")
    suspend fun getAll(): List<ArtistEntity>

    @Query("SELECT * from artists WHERE channel_id IN (:channelIds)")
    suspend fun getByChannelIds(channelIds: List<String>): List<ArtistEntity>
}
