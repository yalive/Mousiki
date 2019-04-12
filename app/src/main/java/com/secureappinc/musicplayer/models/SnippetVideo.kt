package com.secureappinc.musicplayer.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 **********************************
 * Created by Abdelhadi on 4/6/19.
 **********************************
 */
data class SnippetVideo(

        @Expose
        @SerializedName("title")
        val title: String,

        @Expose
        @SerializedName("thumbnails")
        val thumbnails: YTThumbnails,

        @Expose
        @SerializedName("channelTitle")
        val channelTitle: String
)