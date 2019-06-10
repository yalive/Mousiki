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
    val default: YTThumbnailDefault?,

    @Expose
    @SerializedName("medium")
    val medium: YTThumbnailMedium?,

    @Expose
    @SerializedName("high")
    val high: YTThumbnailHigh?
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
data class YTThumbnailDefault(

    @Expose
    @SerializedName("url")
    val url: String
)

@Keep
data class YTThumbnailMedium(

    @Expose
    @SerializedName("url")
    val url: String
)

@Keep
data class YTThumbnailHigh(

    @Expose
    @SerializedName("url")
    val url: String
)
