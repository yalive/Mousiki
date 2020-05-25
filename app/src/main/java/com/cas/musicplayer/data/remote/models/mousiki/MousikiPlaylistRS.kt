package com.cas.musicplayer.data.remote.models.mousiki

import androidx.annotation.Keep
import com.cas.musicplayer.domain.model.MusicTrack
import com.cas.musicplayer.domain.model.toYoutubeDuration
import com.google.gson.annotations.SerializedName

/**
 ***************************************
 * Created by Y.Abdelhadi on 5/25/20.
 ***************************************
 */
@Keep
data class MousikiPlaylistRS(
    @SerializedName("id")
    val id: String?,
    @SerializedName("url")
    val url: String?,
    @SerializedName("title")
    val title: String?,
    @SerializedName("visibility")
    val visibility: String?,
    @SerializedName("description")
    val description: String?,
    @SerializedName("total_items")
    val totalItems: Int?,
    @SerializedName("views")
    val views: Int?,
    @SerializedName("last_updated")
    val lastUpdated: String?,
    @SerializedName("items")
    val items: List<MousikiPlaylistItemDto>?
)

@Keep
data class MousikiPlaylistItemDto(
    @SerializedName("id")
    val id: String?,
    @SerializedName("url")
    val url: String?,
    @SerializedName("url_simple")
    val urlSimple: String?,
    @SerializedName("title")
    val title: String?,
    @SerializedName("thumbnail")
    val thumbnail: String?,
    @SerializedName("duration")
    val duration: String?
)

fun MousikiPlaylistItemDto.toMusicTrack(): MusicTrack {
    return MusicTrack(
        youtubeId = id.orEmpty(),
        duration = MusicTrack.toYoutubeDuration(duration.orEmpty()),
        title = title.orEmpty()
    )
}

fun MousikiPlaylistRS.musicTracks(): List<MusicTrack> {
    return items?.map { it.toMusicTrack() }.orEmpty()
}



