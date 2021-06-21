package com.mousiki.shared.data.models

import com.mousiki.shared.Keep
import kotlinx.serialization.Serializable

/**
 ***************************************
 * Created by Abdelhadi on 2019-06-11.
 ***************************************
 */
@Keep
@Serializable
data class YTBPlaylistItem(
    val snippet: YTBPlaylistItemSnippet? = null,
    val contentDetails: YTBPlaylistItemContentDetail? = null
)

@Keep
@Serializable
data class YTBPlaylistItemSnippet(
    val title: String? = null,

    var thumbnails: YTThumbnails? = null,

    val channelTitle: String? = null
)

@Keep
@Serializable
data class YTBPlaylistItemContentDetail(
    val videoId: String? = null
)
