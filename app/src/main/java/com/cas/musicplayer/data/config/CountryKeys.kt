package com.cas.musicplayer.data.config

import com.google.gson.annotations.SerializedName

/**
 ***************************************
 * Created by Y.Abdelhadi on 4/16/20.
 ***************************************
 */
data class CountryKeys(
    @SerializedName("countryCode")
    val countries: String,
    @SerializedName("keys")
    val keys: List<String>
)