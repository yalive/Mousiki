package com.cas.musicplayer.data.local.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.cas.musicplayer.data.local.models.HistoricTrackEntity

/**
 ***************************************
 * Created by Abdelhadi on 2019-12-01.
 ***************************************
 */
@Dao
interface HistoricTracksDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(musicTrack: HistoricTrackEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(tracks: List<HistoricTrackEntity>)

    @Query("UPDATE historic_tracks SET count=(count+1) WHERE youtube_id=:trackYoutubeId")
    suspend fun incrementPlayCount(trackYoutubeId: String): Int

    @Query("SELECT * FROM historic_tracks ORDER BY count DESC LIMIT :max")
    suspend fun getHeavyList(max: Int = 10): List<HistoricTrackEntity>

    @Query("DELETE  from historic_tracks WHERE youtube_id=:youtubeId ")
    suspend fun deleteMusicTrack(youtubeId: String)

    @Query("SELECT  * from historic_tracks WHERE youtube_id=:youtubeId ")
    suspend fun getByYoutubeId(youtubeId: String): HistoricTrackEntity?

    @Query("SELECT COUNT(*) from historic_tracks")
    suspend fun count(): Int

    @Query("DELETE  from historic_tracks")
    suspend fun clear()
}