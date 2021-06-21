package com.mousiki.shared.data.models

import com.mousiki.shared.Keep
import com.mousiki.shared.Parcelable
import com.mousiki.shared.Parcelize
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 **********************************
 * Created by Abdelhadi on 4/18/19.
 **********************************
 */
@Keep
@Parcelize
@Serializable
data class Artist(
    @SerialName("title")
    val name: String,
    @SerialName("country")
    val countryCode: String? = null,
    val channelId: String,
    @SerialName("thumbUrl")
    val urlImage: String = ""
) : Parcelable {
    val imageFullPath: String
        get() = if (urlImage.startsWith("http")) urlImage else "https://yt3.ggpht.com/-$urlImage"
}