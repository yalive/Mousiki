package com.secureappinc.musicplayer.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 **********************************
 * Created by Abdelhadi on 4/6/19.
 **********************************
 */
data class YTVideoId(

        @Expose
        @SerializedName("kind")
        val kind: String,

        @Expose
        @SerializedName("videoId")
        val videoId: String
)