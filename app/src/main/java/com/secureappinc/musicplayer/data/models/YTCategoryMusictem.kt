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
data class YTCategoryMusictem(
    @Expose
    @SerializedName("kind")
    val kind: String,

    @Expose
    @SerializedName("id")
    val id: YTVideoId,

    @Expose
    @SerializedName("contentDetails")
    val contentDetails: ContentDetailVideo,

    @Expose
    @SerializedName("snippet")
    val snippet: SnippetVideo
)