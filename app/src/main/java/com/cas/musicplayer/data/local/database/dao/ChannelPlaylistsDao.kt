package com.cas.musicplayer.data.local.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.cas.musicplayer.data.local.models.ChannelPlaylistEntity

/**
 ***************************************
 * Created by Abdelhadi on 2019-11-24.
 ***************************************
 */
@Dao
interface ChannelPlaylistsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(playlist: ChannelPlaylistEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(playlists: List<ChannelPlaylistEntity>)

    @Query("SELECT * from channel_playlist WHERE channelId=:channelId")
    suspend fun getChannelPlaylists(channelId: String): List<ChannelPlaylistEntity>
}
