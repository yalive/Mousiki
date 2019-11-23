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
data class YTBPlaylist(
    @Expose
    @SerializedName("id")
    val id: String?,
    @Expose
    @SerializedName("snippet")
    val snippet: YTBPlaylistSnippet?,
    @Expose
    @SerializedName("contentDetails")
    val contentDetails: YTBPlaylistContentDetail?
)

@Keep
data class YTBPlaylistSnippet(
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
data class YTBPlaylistContentDetail(
    @Expose
    @SerializedName("itemCount")
    val itemCount: Int?
)
