package com.cas.musicplayer.data.local.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.cas.musicplayer.data.local.models.ChannelSongEntity

/**
 ***************************************
 * Created by Abdelhadi on 2019-11-24.
 ***************************************
 */
@Dao
interface ChannelSongsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(song: ChannelSongEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(songs: List<ChannelSongEntity>)

    @Query("SELECT * from channel_tracks WHERE channelId=:channelId")
    suspend fun getChannelTracks(channelId: String): List<ChannelSongEntity>
}
