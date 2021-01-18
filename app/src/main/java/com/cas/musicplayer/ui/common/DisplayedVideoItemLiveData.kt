package com.cas.musicplayer.ui.common

import androidx.lifecycle.MutableLiveData
import com.cas.common.resource.Resource
import com.cas.delegatedadapter.DisplayableItem
import com.cas.musicplayer.domain.model.MusicTrack
import com.cas.musicplayer.ui.home.model.DisplayedVideoItem

/**
 ***************************************
 * Created by Y.Abdelhadi on 5/3/20.
 ***************************************
 */

fun MutableLiveData<Resource<List<DisplayableItem>>>.songList(): List<MusicTrack> {
    return (this.value as? Resource.Success)?.data?.filterIsInstance<DisplayedVideoItem>()?.map { it.track }
        ?: emptyList()
}