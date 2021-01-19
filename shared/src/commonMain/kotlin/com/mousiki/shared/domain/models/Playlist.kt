package com.mousiki.shared.domain.models

import com.mousiki.shared.Parcelable
import com.mousiki.shared.Parcelize

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