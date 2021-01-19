@file:Suppress("UNREACHABLE_CODE")

package com.cas.musicplayer.data.remote.models

import androidx.annotation.Keep
import kotlinx.serialization.Serializable

/**
 **********************************
 * Created by Abdelhadi on 4/6/19.
 **********************************
 */
@Keep
@Serializable
data class YTThumbnails(
    val default: YTThumbnail? = null,
    val medium: YTThumbnail? = null,
    val high: YTThumbnail? = null,
    val standard: YTThumbnail? = null,
    val maxres: YTThumbnail? = null
)

fun YTThumbnails.urlOrEmpty(): String {
    medium?.url?.let { return it }
    default?.url?.let { return it }
    high?.url?.let { return it }
    return ""
}

@Keep
@Serializable
data class YTThumbnail(val url: String? = null)