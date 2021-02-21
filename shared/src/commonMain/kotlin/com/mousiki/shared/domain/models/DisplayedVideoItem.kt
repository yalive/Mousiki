package com.mousiki.shared.domain.models

/**
 ***************************************
 * Created by Abdelhadi on 2019-11-11.
 ***************************************
 */
data class DisplayedVideoItem(
    val track: MusicTrack,
    val songTitle: String,
    val songDuration: String,
    val songImagePath: String,
    val isCurrent: Boolean = false,
    val isPlaying: Boolean = false,
    val beforeCurrent: Boolean = false
) : DisplayableItem

fun MusicTrack.toDisplayedVideoItem() = DisplayedVideoItem(
    track = this,
    songTitle = title,
    songDuration = durationFormatted,
    songImagePath = imgUrl
)

fun DisplayedVideoItem.artistName(): String {
    return songTitle.split("-")[0]
}