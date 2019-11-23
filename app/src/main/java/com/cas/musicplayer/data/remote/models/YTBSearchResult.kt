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
data class YTBSearchResult(
    @Expose
    @SerializedName("id")
    val id: YTBSearchResultId?,
    @Expose
    @SerializedName("snippet")
    val snippet: YTBSearchResultSnippet?
)

@Keep
data class YTBSearchResultId(
    @Expose
    @SerializedName("kind")
    val kind: String?,
    @Expose
    @SerializedName("videoId")
    val videoId: String?,
    @Expose
    @SerializedName("channelId")
    val channelId: String?,
    @Expose
    @SerializedName("playlistId")
    val playlistId: String?
)

@Keep
data class YTBSearchResultSnippet(
    @Expose
    @SerializedName("title")
    val title: String?,

    @Expose
    @SerializedName("thumbnails")
    var thumbnails: YTThumbnails?
)