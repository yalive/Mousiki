package com.cas.musicplayer.ui.common.multiselection

import com.mousiki.shared.domain.models.DisplayableItem
import com.mousiki.shared.domain.models.Track

data class SelectableTrack(
    val track: Track,
    val selected: Boolean
) : DisplayableItem
