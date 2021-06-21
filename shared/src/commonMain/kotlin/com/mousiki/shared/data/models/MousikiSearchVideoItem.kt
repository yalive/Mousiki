package com.mousiki.shared.data.models

import com.mousiki.shared.Keep
import com.mousiki.shared.domain.models.MusicTrack
import com.mousiki.shared.domain.models.toYoutubeDuration
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 ***************************************
 * Created by Y.Abdelhadi on 5/9/20.
 ***************************************
 */
@Keep
@Serializable
data class MousikiSearchVideoItem(
    val id: String? = null,
    val title: String? = null,
    val url: String? = null,
    val duration: String? = null,
    @SerialName("uploadDate")
    val upload_date: String? = null,
    val thumbnail: String? = null,
    val views: String? = null
)

fun MousikiSearchVideoItem.toMusicTrack(owner: VideoOwner?): MusicTrack {
    var artistName = owner?.title.orEmpty()
    if (artistName.isEmpty()) {
        artistName = title.orEmpty().split("-")[0]
    }
    return MusicTrack(
        youtubeId = id.orEmpty(),
        duration = MusicTrack.toYoutubeDuration(duration.orEmpty()),
        title = title.orEmpty(),
        artistName = artistName
    )
}