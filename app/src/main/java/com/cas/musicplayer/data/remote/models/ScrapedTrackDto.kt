package com.cas.musicplayer.data.remote.models

import androidx.annotation.Keep
import com.cas.musicplayer.domain.model.MusicTrack
import com.cas.musicplayer.domain.model.toYoutubeDuration
import com.google.gson.annotations.SerializedName

/**
 ***************************************
 * Created by Y.Abdelhadi on 5/9/20.
 ***************************************
 */
@Keep
data class ScrapedTrackDto(
    @SerializedName("id")
    val id: String?,
    @SerializedName("title")
    val title: String?,
    @SerializedName("url")
    val url: String?,
    @SerializedName("duration")
    val duration: String?,
    @SerializedName("snippet")
    val snippet: String?,
    @SerializedName("upload_date")
    val upload_date: String?,
    @SerializedName("thumbnail_src")
    val thumbnail_src: String?,
    @SerializedName("views")
    val views: String?
)

fun ScrapedTrackDto.toMusicTrack(): MusicTrack {
    return MusicTrack(
        youtubeId = id.orEmpty(),
        duration = MusicTrack.toYoutubeDuration(duration.orEmpty()),
        title = title.orEmpty()
    )
}