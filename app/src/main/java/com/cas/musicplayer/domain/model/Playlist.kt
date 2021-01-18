package com.cas.musicplayer.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 ***************************************
 * Created by Abdelhadi on 2019-06-12.
 ***************************************
 */

@Parcelize
data class Playlist(
    val id: String,
    val title: String,
    val itemCount: Int,
    val urlImage: String
) : Parcelable