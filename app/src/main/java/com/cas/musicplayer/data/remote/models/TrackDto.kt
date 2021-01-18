package com.cas.musicplayer.data.remote.models

import com.cas.musicplayer.domain.model.MusicTrack
import com.google.gson.annotations.SerializedName

/**
 ***************************************
 * Created by Y.Abdelhadi on 4/16/20.
 ***************************************
 */
data class TrackDto(
    @SerializedName("youtubeId")
    val youtubeId: String?,
    @SerializedName("title")
    val title: String?,
    @SerializedName("duration")
    val duration: String?
)

fun TrackDto.toDomainModel() = MusicTrack(
    youtubeId = youtubeId.orEmpty(),
    duration = duration.orEmpty(),
    title = title.orEmpty()
)