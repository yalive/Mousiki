package com.mousiki.shared.player

import com.mousiki.shared.domain.models.DisplayableItem
import com.mousiki.shared.domain.models.DisplayedVideoItem

fun PlaySongDelegate.updateCurrentPlaying(items: List<DisplayableItem>): List<DisplayableItem> {
    return items.map { item ->
        when (item) {
            is DisplayedVideoItem -> {
                val isCurrent = currentSong?.id == item.track.id
                item.copy(
                    isCurrent = isCurrent,
                    isPlaying = isCurrent && isPlayingASong()
                )
            }
            else -> item
        }
    }
}