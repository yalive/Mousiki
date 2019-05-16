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
data class YTTrendingItem(

    @Expose
    @SerializedName("kind")
    val kind: String,

    @Expose
    @SerializedName("snippet")
    val snippet: SnippetVideo?,

    @Expose
    @SerializedName("contentDetails")
    val contentDetails: ContentDetailVideo,

    @Expose
    @SerializedName("id")
    val id: String
) {
    fun snippetTitle(): String {
        if (snippet == null) return ""
        return snippet.title
    }
}