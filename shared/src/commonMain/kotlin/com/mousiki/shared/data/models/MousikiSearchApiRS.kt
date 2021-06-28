package com.mousiki.shared.data.models

import com.mousiki.shared.Keep
import com.mousiki.shared.domain.models.YtbTrack
import com.mousiki.shared.domain.models.SearchTracksResult
import kotlinx.serialization.Serializable

/**
 ***************************************
 * Created by Y.Abdelhadi on 5/9/20.
 ***************************************
 */
@Keep
@Serializable
data class MousikiSearchApiRS(
    val results: List<MousikiSearchApiResult>? = null,
    val nextPageToken: String? = null,
    val key: String? = null
)

@Keep
@Serializable
data class MousikiSearchApiResult(
    val video: MousikiSearchVideoItem? = null,
    val owner: VideoOwner? = null
)

fun MousikiSearchApiRS.tracks(): List<YtbTrack> {
    return results?.mapNotNull { it.video?.toMusicTrack(it.owner) }
        ?.filter { it.duration.isNotEmpty() && it.youtubeId.isNotEmpty() }
        ?: emptyList()
}

fun MousikiSearchApiRS.searchResults(): SearchTracksResult {
    val trackList = (results?.mapNotNull { it.video?.toMusicTrack(it.owner) }
        ?.filter { it.duration.isNotEmpty() && it.youtubeId.isNotEmpty() }
        ?: emptyList())
    return SearchTracksResult(trackList, nextPageToken.orEmpty(), key.orEmpty())
}