package com.cas.musicplayer.ui.home.model

import com.mousiki.shared.domain.models.DisplayableItem
import com.mousiki.shared.domain.models.MusicTrack
import com.mousiki.shared.domain.models.durationFormatted
import com.mousiki.shared.domain.models.imgUrl

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