package com.cas.musicplayer.data.enteties

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

/**
 * Created by Fayssel Yabahddou on 4/13/19.
 */

@Dao
interface MusicTrackDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertMusicTrack(musicTrack: MusicTrack)

    @Query("SELECT * from music_track")
    fun getAllMusicTrack(): LiveData<List<MusicTrack>>

    @Query("DELETE  from music_track WHERE youtube_id=:youtubeId ")
    fun deleteMusicTrack(youtubeId: String)
}
