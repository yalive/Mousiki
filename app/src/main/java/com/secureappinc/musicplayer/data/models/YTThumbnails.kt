@file:Suppress("UNREACHABLE_CODE")

package com.secureappinc.musicplayer.data.models

import androidx.annotation.Keep
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 **********************************
 * Created by Abdelhadi on 4/6/19.
 **********************************
 */
@Keep
data class YTThumbnails(
    @Expose
    @SerializedName("default")
    val default: YTThumbnail?,

    @Expose
    @SerializedName("medium")
    val medium: YTThumbnail?,

    @Expose
    @SerializedName("high")
    val high: YTThumbnail?,

    @Expose
    @SerializedName("standard")
    val standard: YTThumbnail?,

    @Expose
    @SerializedName("maxres")
    val maxres: YTThumbnail?
)

fun YTThumbnails.urlOrEmpty(): String {

    medium?.url?.let {
        return it
    }

    default?.url?.let {
        return it
    }

    high?.url?.let {
        return it
    }

    return ""
}

@Keep
data class YTThumbnail(
    @Expose
    @SerializedName("url")
    val url: String
)