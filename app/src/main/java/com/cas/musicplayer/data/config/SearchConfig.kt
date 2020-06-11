package com.cas.musicplayer.data.config

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

/**
 ************************************
 * Created by Abdelhadi on 6/10/20.
 * Copyright © 2020 Mousiki
 ************************************
 */
@Keep
data class SearchConfig(
    @SerializedName("apis")
    val apis: List<String>?,
    @SerializedName("maxApiToTry")
    val maxApi: Int?,
    @SerializedName("retryCount")
    val retryCount: Int?
)

// For Fallback
fun SearchConfig.apiList(): List<String> {
    if (!apis.isNullOrEmpty()) return apis
    return listOf("https://mousikiapp.herokuapp.com/")
}

fun SearchConfig.maxApi(): Int {
    if (maxApi != null && maxApi > 0) return maxApi
    return 2
}

fun SearchConfig.retryCount(): Int {
    if (retryCount != null && retryCount > 0) return retryCount
    return 1
}