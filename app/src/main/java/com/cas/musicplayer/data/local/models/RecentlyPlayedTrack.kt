package com.cas.musicplayer.data.local.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.cas.musicplayer.domain.model.MusicTrack

/**
 ***************************************
 * Created by Abdelhadi on 2019-11-26.
 ***************************************
 */
@Entity(tableName = "recent_played_tracks", indices = [Index(unique = true, value = arrayOf("youtube_id"))])
data class RecentlyPlayedTrack(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,
    @ColumnInfo(name = "youtube_id") val youtubeId: String,
    val title: String,
    val duration: String
) {
    companion object {
        fun fromMusicTrack(track: MusicTrack) = RecentlyPlayedTrack(
            youtubeId = track.youtubeId,
            title = track.title,
            duration = track.duration
        )
    }
}


fun RecentlyPlayedTrack.toMusicTrack() = MusicTrack(
    youtubeId = youtubeId,
    title = title,
    duration = duration
)