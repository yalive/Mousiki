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
data class YTBPlaylistItem(
    @Expose
    @SerializedName("snippet")
    val snippet: YTBPlaylistItemSnippet?,
    @Expose
    @SerializedName("contentDetails")
    val contentDetails: YTBPlaylistItemContentDetail?
)

@Keep
data class YTBPlaylistItemSnippet(
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
data class YTBPlaylistItemContentDetail(
    @Expose
    @SerializedName("videoId")
    val videoId: String?
)
