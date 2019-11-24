package com.cas.musicplayer.data.local.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.cas.musicplayer.data.local.models.TrendingSongEntity
import com.cas.musicplayer.domain.model.MusicTrack

/**
 ***************************************
 * Created by Abdelhadi on 2019-11-20.
 ***************************************
 */

@Dao
interface TrendingSongsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(musicTrack: TrendingSongEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(tracks: List<TrendingSongEntity>)

    @Query("SELECT * from trending_tracks limit :max")
    suspend fun getSongs(max: Int = 25): List<TrendingSongEntity>

    @Query("SELECT * from trending_tracks WHERE id > :id limit :max")
    suspend fun getSongsStartingFrom(id: Int, max: Int = 25): List<TrendingSongEntity>

    @Query("DELETE  from trending_tracks WHERE youtube_id=:youtubeId ")
    suspend fun deleteMusicTrack(youtubeId: String)

    @Query("SELECT  * from trending_tracks WHERE youtube_id=:youtubeId ")
    suspend fun getByYoutubeId(youtubeId: String): TrendingSongEntity

    @Query("SELECT COUNT(*) from trending_tracks")
    suspend fun count(): Int

    @Query("DELETE  from trending_tracks")
    suspend fun clear()
}

fun TrendingSongEntity.toMusicTrack() = MusicTrack(
    youtubeId = youtubeId,
    title = title,
    duration = duration
)
