package com.cas.musicplayer.data.remote.models

import android.os.Parcelable
import androidx.annotation.Keep
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

/**
 **********************************
 * Created by Abdelhadi on 4/18/19.
 **********************************
 */
@Keep
@Parcelize
data class Artist(
    @Expose
    @SerializedName("name")
    val name: String,
    @Expose
    @SerializedName("country")
    val countryCode: String,
    @Expose
    @SerializedName("channel")
    val channelId: String,
    var urlImage: String = ""
) : Parcelable