package com.cas.musicplayer.data.local.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.cas.musicplayer.data.local.models.FavouriteSongEntity

/**
 * Created by Fayssel Yabahddou on 4/13/19.
 */

@Dao
interface FavouriteTracksDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertMusicTrack(musicTrack: FavouriteSongEntity)

    @Query("SELECT * from favourite_tracks ORDER BY id DESC LIMIT :max")
    fun getSongsLive(max: Int): LiveData<List<FavouriteSongEntity>>

    @Query("SELECT * from favourite_tracks")
    fun getAll(): List<FavouriteSongEntity>

    @Query("SELECT * from favourite_tracks ORDER BY id DESC LIMIT :max")
    fun getSongs(max: Int): List<FavouriteSongEntity>

    @Query("DELETE  from favourite_tracks WHERE youtube_id=:youtubeId ")
    fun deleteSong(youtubeId: String)
}
