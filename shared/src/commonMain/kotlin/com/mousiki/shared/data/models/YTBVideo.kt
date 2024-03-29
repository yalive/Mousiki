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
data class YTBVideo(
    val id: String? = null,
    val snippet: YTBVideoSnippet? = null,
    val contentDetails: YTBVideoContentDetail? = null
)

@Keep
@Serializable
data class YTBVideoSnippet(
    val title: String? = null,
    var thumbnails: YTThumbnails? = null,
    val channelTitle: String? = null,
    val channelId: String? = null,
)

@Keep
@Serializable
data class YTBVideoContentDetail(
    val duration: String? = null
)
