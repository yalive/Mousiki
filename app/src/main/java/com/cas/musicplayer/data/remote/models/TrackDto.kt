package com.cas.musicplayer.data.remote.models

import androidx.annotation.Keep
import com.cas.musicplayer.domain.model.MusicTrack
import kotlinx.serialization.Serializable

/**
 ***************************************
 * Created by Y.Abdelhadi on 4/16/20.
 ***************************************
 */

@Keep
@Serializable
data class TrackDto(
    val youtubeId: String? = null,
    val title: String? = null,
    val duration: String? = null
)

fun TrackDto.toDomainModel() = MusicTrack(
    youtubeId = youtubeId.orEmpty(),
    duration = duration.orEmpty(),
    title = title.orEmpty()
)