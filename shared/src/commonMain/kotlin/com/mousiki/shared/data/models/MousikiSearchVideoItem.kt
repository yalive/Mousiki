package com.mousiki.shared.data.models

import com.mousiki.shared.Keep
import com.mousiki.shared.domain.models.YtbTrack
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

fun MousikiSearchVideoItem.toMusicTrack(owner: VideoOwner?): YtbTrack {
    var artistName = owner?.title.orEmpty()
    if (artistName.isEmpty()) {
        artistName = title.orEmpty().split("-")[0]
    }
    return YtbTrack(
        youtubeId = id.orEmpty(),
        duration = YtbTrack.toYoutubeDuration(duration.orEmpty()),
        title = title.orEmpty(),
        artistName = artistName,
        artistId = owner?.channelId.orEmpty()
    )
}