package com.secureappinc.musicplayer.data.models

import androidx.annotation.Keep
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 ***************************************
 * Created by Abdelhadi on 2019-06-11.
 ***************************************
 */
@Keep
data class YTBChannel(
    @Expose
    @SerializedName("id")
    val id: String?,
    @Expose
    @SerializedName("snippet")
    val snippet: YTBChannelSnippet?
)

@Keep
data class YTBChannelSnippet(
    @Expose
    @SerializedName("title")
    val title: String?,

    @Expose
    @SerializedName("thumbnails")
    var thumbnails: YTThumbnails?
)