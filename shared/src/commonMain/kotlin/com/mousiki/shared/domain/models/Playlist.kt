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
    val urlImage: String,
    val type: String
) : Parcelable {

    companion object {
        const val TYPE_FAV = "FAV"
        const val TYPE_RECENT = "RECENT"
        const val TYPE_HEAVY = "HEAVY"
        const val TYPE_CUSTOM = "CUSTOM"
        const val TYPE_YTB = "YTB"

        const val CREATED_BY_USER = "USER"
        const val CREATED_BY_APP = "Mousiki"
    }
}

val Playlist.editable: Boolean
    get() = type == Playlist.TYPE_CUSTOM || type == Playlist.TYPE_FAV

val Playlist.isCustom: Boolean
    get() = type == Playlist.TYPE_CUSTOM

val Playlist.isFavourite: Boolean
    get() = type == Playlist.TYPE_FAV

val Playlist.isRecent: Boolean
    get() = type == Playlist.TYPE_RECENT

val Playlist.isHeavy: Boolean
    get() = type == Playlist.TYPE_HEAVY

val Playlist.isYtb: Boolean
    get() = type == Playlist.TYPE_YTB
