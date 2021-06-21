package com.mousiki.shared.ui.resource

import com.mousiki.shared.domain.models.DisplayableItem
import com.mousiki.shared.domain.models.DisplayedVideoItem
import com.mousiki.shared.domain.models.MusicTrack
import kotlinx.coroutines.flow.MutableStateFlow

fun MutableStateFlow<Resource<List<DisplayableItem>>?>.songList(): List<MusicTrack> {
    return (this.value as? Resource.Success)?.data?.filterIsInstance<DisplayedVideoItem>()
        ?.map { it.track }
        ?: emptyList()
}