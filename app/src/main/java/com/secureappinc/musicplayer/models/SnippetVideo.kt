package com.secureappinc.musicplayer.models

import androidx.annotation.Keep
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 **********************************
 * Created by Abdelhadi on 4/6/19.
 **********************************
 */
@Keep
data class SnippetVideo(

    @Expose
    @SerializedName("title")
    val title: String,

    @Expose
    @SerializedName("thumbnails")
    var thumbnails: YTThumbnails? = null,

    @Expose
    @SerializedName("channelTitle")
    val channelTitle: String
) {
    fun urlImageOrEmpty(): String {
        if (thumbnails != null) {
            return thumbnails!!.urlOrEmpty()
        }
        return ""
    }
}
