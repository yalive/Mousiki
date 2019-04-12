package com.secureappinc.musicplayer.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 **********************************
 * Created by Abdelhadi on 4/6/19.
 **********************************
 */
data class YTThumbnails(
        @Expose
        @SerializedName("default")
        val default: YTThumbnailDefault,

        @Expose
        @SerializedName("medium")
        val medium: YTThumbnailMedium,

        @Expose
        @SerializedName("high")
        val high: YTThumbnailHigh
)

data class YTThumbnailDefault(

        @Expose
        @SerializedName("url")
        val url: String
)


data class YTThumbnailMedium(

        @Expose
        @SerializedName("url")
        val url: String
)

data class YTThumbnailHigh(

        @Expose
        @SerializedName("url")
        val url: String
)