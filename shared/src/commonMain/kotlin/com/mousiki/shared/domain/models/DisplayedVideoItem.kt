package com.mousiki.shared.domain.models

import com.mousiki.shared.player.PlaySongDelegate

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

fun MusicTrack.toDisplayedVideoItem(
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

fun MusicTrack.toDisplayedVideoItem(
    playDelegate: PlaySongDelegate
): DisplayedVideoItem {
    val isCurrent = playDelegate.currentSong?.youtubeId == youtubeId
    return toDisplayedVideoItem(
        isCurrent = isCurrent,
        isPlaying = isCurrent && playDelegate.isPlayingASong()
    )
}

fun List<MusicTrack>.toDisplayedVideoItems(
    playDelegate: PlaySongDelegate
): List<DisplayedVideoItem> {
    return map { it.toDisplayedVideoItem(playDelegate) }
}

fun DisplayedVideoItem.artistName(): String {
    return songTitle.split("-")[0]
}