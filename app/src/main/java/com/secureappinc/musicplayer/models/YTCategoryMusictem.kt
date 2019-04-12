package com.secureappinc.musicplayer.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 **********************************
 * Created by Abdelhadi on 4/6/19.
 **********************************
 */
data class YTCategoryMusictem(
        @Expose
        @SerializedName("kind")
        val kind: String,

        @Expose
        @SerializedName("id")
        val id: YTVideoId,

        @Expose
        @SerializedName("snippet")
        val snippet: SnippetVideo
)