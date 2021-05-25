package com.mousiki.shared.data.models

import com.mousiki.shared.Keep
import kotlinx.serialization.Serializable

@Keep
@Serializable
data class AudioInfoRS(
    val url: String? = null
)