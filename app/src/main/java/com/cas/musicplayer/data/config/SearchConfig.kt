package com.cas.musicplayer.data.config

import androidx.annotation.Keep
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 ************************************
 * Created by Abdelhadi on 6/10/20.
 * Copyright © 2020 Mousiki
 ************************************
 */
@Keep
@Serializable
data class SearchConfig(
    val apis: List<String>? = null,
    @SerialName("maxApiToTry")
    val maxApi: Int? = null,
    val retryCount: Int? = null
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
