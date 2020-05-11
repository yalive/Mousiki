package com.cas.musicplayer.data.remote.models

import androidx.annotation.Keep
import com.cas.musicplayer.domain.model.MusicTrack
import com.google.gson.annotations.SerializedName

/**
 ***************************************
 * Created by Y.Abdelhadi on 5/9/20.
 ***************************************
 */
@Keep
data class MousikiSearchApiRS(
    @SerializedName("results")
    val results: List<MousikiSearchApiResult>?
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