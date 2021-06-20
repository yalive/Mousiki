package com.mousiki.shared.domain.models

import com.mousiki.shared.player.PlaySongDelegate

/**
 ***************************************
 * Created by Abdelhadi on 2019-11-11.
 ***************************************
 */
data class DisplayedVideoItem(
    val track: Track,
    val songTitle: String,
    val songDuration: String,
    val songImagePath: String,
    val isCurrent: Boolean = false,
    val isPlaying: Boolean = false,
    val beforeCurrent: Boolean = false
) : DisplayableItem

fun Track.toDisplayedVideoItem() = DisplayedVideoItem(
    track = this,
    songTitle = title,
    songDuration = durationFormatted,
    songImagePath = imgUrl
)

fun Track.toDisplayedVideoItem(
    isCurrent: Boolean = false,
    isPlaying: Boolean = false,
) = DisplayedVideoItem(
    track = this,
    songTitle = title,
    songDuration = durationFormatted,
    songImagePath = imgUrl,
    isCurrent = isCurrent,
    isPlaying = isPlaying
)

fun Track.toDisplayedVideoItem(
    playDelegate: PlaySongDelegate
): DisplayedVideoItem {
    val isCurrent = playDelegate.currentSong?.id == id
    return toDisplayedVideoItem(
        isCurrent = isCurrent,
        isPlaying = isCurrent && playDelegate.isPlayingASong()
    )
}

fun List<Track>.toDisplayedVideoItems(
    playDelegate: PlaySongDelegate
): List<DisplayedVideoItem> {
    return map { it.toDisplayedVideoItem(playDelegate) }
}

// TODO: get real artist name
fun DisplayedVideoItem.artistName(): String {
    return track.artistName
}