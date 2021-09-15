package com.cas.musicplayer.ui.local.videos.queue

import com.cas.musicplayer.delegateadapter.MousikiAdapter
import com.cas.musicplayer.ui.popular.SongItemDiffUtil
import com.mousiki.shared.domain.models.Track

class LocalVideoQueueAdapter(
    onClickTrack: (Track) -> Unit,
) : MousikiAdapter(
    listOf(LocalVideoQueueAdapterDelegate(onClickTrack)),
    SongItemDiffUtil()
)