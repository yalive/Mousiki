package com.secureappinc.musicplayer.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 **********************************
 * Created by Abdelhadi on 4/6/19.
 **********************************
 */
data class ContentDetailVideo(
        @Expose
        @SerializedName("duration")
        val duration: String,

        @Expose
        @SerializedName("itemCount")
        val itemCount: Int,

        @Expose
        @SerializedName("dimension")
        val dimension: String
)