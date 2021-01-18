package com.cas.musicplayer.data.remote.models

import android.os.Parcelable
import androidx.annotation.Keep
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

/**
 **********************************
 * Created by Abdelhadi on 4/18/19.
 **********************************
 */
@Keep
@Parcelize
data class Artist(
    @Expose
    @SerializedName("title")
    val name: String,
    @Expose
    @SerializedName("country")
    val countryCode: String,
    @Expose
    @SerializedName("channelId")
    val channelId: String,
    @Expose
    @SerializedName("thumbUrl")
    val urlImage: String = ""
) : Parcelable {
    val imageFullPath: String
        get() = if (urlImage.startsWith("http")) urlImage else "https://yt3.ggpht.com/-$urlImage"
}