package com.cas.musicplayer.data.local.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.cas.musicplayer.domain.model.MusicTrack

/**
 ***************************************
 * Created by Abdelhadi on 2019-11-24.
 ***************************************
 */
@Entity(tableName = "song_titles")
data class SongTitleEntity(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,
    val playlistId: String,
    val title: String
)

fun SongTitleEntity.toMusicTrack() = MusicTrack(
    youtubeId = "",
    title = title,
    duration = ""
)