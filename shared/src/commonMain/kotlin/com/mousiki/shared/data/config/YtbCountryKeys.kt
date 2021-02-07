package com.mousiki.shared.data.config

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 ***************************************
 * Created by Y.Abdelhadi on 4/16/20.
 ***************************************
 */
@Serializable
data class YtbCountryKeys(
    @SerialName("countryCode")
    val countries: String,
    @SerialName("keys")
    val keys: List<String>
)