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
data class YTVideoId(

    @Expose
    @SerializedName("kind")
    val kind: String,

    @Expose
    @SerializedName("channelId")
    val channelId: String,

    @Expose
    @SerializedName("playlistId")
    val playlistId: String,

    @Expose
    @SerializedName("videoId")
    val videoId: String
)