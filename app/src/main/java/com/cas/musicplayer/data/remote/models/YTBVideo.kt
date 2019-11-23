package com.cas.musicplayer.data.remote.models

import androidx.annotation.Keep
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 ***************************************
 * Created by Abdelhadi on 2019-06-11.
 ***************************************
 */
@Keep
data class YTBVideo(
    @Expose
    @SerializedName("id")
    val id: String?,
    @Expose
    @SerializedName("snippet")
    val snippet: YTBVideoSnippet?,
    @Expose
    @SerializedName("contentDetails")
    val contentDetails: YTBVideoContentDetail?
)

@Keep
data class YTBVideoSnippet(
    @Expose
    @SerializedName("title")
    val title: String?,

    @Expose
    @SerializedName("thumbnails")
    var thumbnails: YTThumbnails?,

    @Expose
    @SerializedName("channelTitle")
    val channelTitle: String?
)

@Keep
data class YTBVideoContentDetail(
    @Expose
    @SerializedName("duration")
    val duration: String?
)
