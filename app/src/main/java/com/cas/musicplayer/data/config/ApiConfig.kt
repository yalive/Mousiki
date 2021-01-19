package com.cas.musicplayer.data.config

import androidx.annotation.Keep
import kotlinx.serialization.Serializable

/**
 ************************************
 * Created by Abdelhadi on 6/10/20.
 * Copyright © 2020 Mousiki
 ************************************
 */
@Keep
@Serializable
data class ApiConfig(
    val search: SearchConfig,
    val artistSongs: SearchConfig,
    val playlists: SearchConfig,
    val home: SearchConfig
)