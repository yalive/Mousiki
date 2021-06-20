package com.mousiki.shared.data.models

import com.mousiki.shared.Keep
import com.mousiki.shared.domain.models.MusicTrack
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
    title = title.orEmpty(),
    artistName = title.orEmpty().split("-")[0]
)