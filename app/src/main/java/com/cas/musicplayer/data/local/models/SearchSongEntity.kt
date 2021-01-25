package com.cas.musicplayer.data.local.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.mousiki.shared.domain.models.MusicTrack

/**
 ***************************************
 * Created by Abdelhadi on 2019-12-09.
 ***************************************
 */
@Entity(
    tableName = "songs_search_result",
    indices = [Index(unique = true, value = arrayOf("youtube_id"))]
)
data class SearchSongEntity(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,
    @ColumnInfo(name = "youtube_id") val youtubeId: String,
    val title: String,
    val duration: String,
    val query: String
)

fun SearchSongEntity.toMusicTrack() = MusicTrack(
    youtubeId = youtubeId,
    title = title,
    duration = duration
)