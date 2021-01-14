package com.cas.musicplayer.data.remote.models.mousiki

import androidx.annotation.Keep
import com.cas.musicplayer.domain.model.MusicTrack
import com.cas.musicplayer.domain.model.SearchTracksResult
import com.google.gson.annotations.SerializedName

/**
 ***************************************
 * Created by Y.Abdelhadi on 5/9/20.
 ***************************************
 */
@Keep
data class MousikiSearchApiRS(
    @SerializedName("results")
    val results: List<MousikiSearchApiResult>?,
    @SerializedName("nextPageToken")
    val nextPageToken: String?,
    @SerializedName("key")
    val key: String?
)

@Keep
data class MousikiSearchApiResult(
    @SerializedName("video")
    val video: MousikiSearchVideoItem?
)

fun MousikiSearchApiRS.tracks(): List<MusicTrack> {
    return results?.mapNotNull { it.video?.toMusicTrack() }
        ?.filter { it.duration.isNotEmpty() && it.youtubeId.isNotEmpty() }
        ?: emptyList()
}

fun MousikiSearchApiRS.searchResults(): SearchTracksResult {
    val trackList = (results?.mapNotNull { it.video?.toMusicTrack() }
        ?.filter { it.duration.isNotEmpty() && it.youtubeId.isNotEmpty() }
        ?: emptyList())
    return SearchTracksResult(trackList, nextPageToken.orEmpty(), key.orEmpty())
}