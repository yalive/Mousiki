package com.cas.musicplayer.data.local.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.cas.musicplayer.data.local.models.RecentlyPlayedTrack

/**
 ***************************************
 * Created by Abdelhadi on 2019-11-26.
 ***************************************
 */

@Dao
interface RecentlyPlayedTracksDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(musicTrack: RecentlyPlayedTrack)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(tracks: List<RecentlyPlayedTrack>)

    @Query("SELECT * FROM recent_played_tracks ORDER BY id DESC LIMIT :max")
    suspend fun getSongs(max: Int = 100): List<RecentlyPlayedTrack>

    @Query("SELECT * from recent_played_tracks ORDER BY id DESC LIMIT :max")
    fun getSongsLive(max: Int): LiveData<List<RecentlyPlayedTrack>>

    @Query("DELETE  from recent_played_tracks WHERE youtube_id=:youtubeId ")
    suspend fun deleteMusicTrack(youtubeId: String)

    @Query("SELECT  * from recent_played_tracks WHERE youtube_id=:youtubeId ")
    suspend fun getByYoutubeId(youtubeId: String): RecentlyPlayedTrack

    @Query("SELECT COUNT(*) from recent_played_tracks")
    suspend fun count(): Int

    @Query("DELETE  from recent_played_tracks")
    suspend fun clear()
}