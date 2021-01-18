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
data class ApiConfig(
    @SerializedName("search")
    val search: SearchConfig,
    @SerializedName("artistSongs")
    val artistSongs: SearchConfig,
    @SerializedName("playlists")
    val playlists: SearchConfig,
    @SerializedName("home")
    val home: SearchConfig
)