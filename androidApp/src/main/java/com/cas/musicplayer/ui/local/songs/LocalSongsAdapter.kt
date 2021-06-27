package com.cas.musicplayer.ui.local.songs

import com.cas.musicplayer.delegateadapter.MousikiAdapter
import com.cas.musicplayer.ui.popular.SongItemDiffUtil
import com.mousiki.shared.domain.models.Track

class LocalSongsAdapter(
    onClickTrack: (Track) -> Unit
) : MousikiAdapter(
    listOf(LocalSongsAdapterDelegate(onClickTrack)),
    SongItemDiffUtil()
)