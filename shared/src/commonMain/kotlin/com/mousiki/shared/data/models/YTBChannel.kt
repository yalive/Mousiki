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
data class YTBChannel(
    val id: String? = null,
    val snippet: YTBChannelSnippet? = null
)

@Keep
@Serializable
data class YTBChannelSnippet(
    val title: String? = null,
    var thumbnails: YTThumbnails? = null
)