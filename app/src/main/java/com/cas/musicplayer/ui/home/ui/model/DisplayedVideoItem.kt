package com.cas.musicplayer.ui.home.ui.model

import com.cas.musicplayer.data.enteties.MusicTrack

/**
 ***************************************
 * Created by Abdelhadi on 2019-11-11.
 ***************************************
 */
data class DisplayedVideoItem(
    val track: MusicTrack,
    val songTitle: String,
    val songDuration: String,
    val songImagePath: String
)

fun MusicTrack.toDisplayedVideoItem() = DisplayedVideoItem(
    track = this,
    songTitle = title,
    songDuration = durationFormatted,
    songImagePath = imgUrl
)