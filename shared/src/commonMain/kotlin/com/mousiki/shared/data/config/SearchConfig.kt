package com.mousiki.shared.data.config

import com.mousiki.shared.Keep
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

private val forceDemo = true

// For Fallback
fun SearchConfig.apiList(): List<String> {
    if (forceDemo) {
        return listOf("https://ktor-demo.herokuapp.com")
    }
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
