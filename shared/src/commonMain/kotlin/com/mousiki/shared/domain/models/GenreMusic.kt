package com.mousiki.shared.domain.models

import com.mousiki.shared.Parcelable
import com.mousiki.shared.Parcelize

@Parcelize
data class GenreMusic(
    val title: String,
    val imageName: String,
    val channelId: String,
    val topTracksPlaylist: String,
    val isMood: Boolean,
    val backgroundColor: String
) : Parcelable, DisplayableItem
