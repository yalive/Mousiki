package com.cas.musicplayer.ui.common

import androidx.lifecycle.MutableLiveData
import com.mousiki.shared.ui.resource.Resource
import com.mousiki.shared.domain.models.DisplayedVideoItem
import com.mousiki.shared.domain.models.DisplayableItem
import com.mousiki.shared.domain.models.MusicTrack

/**
 ***************************************
 * Created by Y.Abdelhadi on 5/3/20.
 ***************************************
 */

fun MutableLiveData<Resource<List<DisplayableItem>>>.songList(): List<MusicTrack> {
    return (this.value as? Resource.Success)?.data?.filterIsInstance<DisplayedVideoItem>()
        ?.map { it.track }
        ?: emptyList()
}