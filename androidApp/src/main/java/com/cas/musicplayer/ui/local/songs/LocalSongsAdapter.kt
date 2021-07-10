package com.cas.musicplayer.ui.local.songs

import com.cas.musicplayer.delegateadapter.MousikiAdapter
import com.cas.musicplayer.ui.popular.SongItemDiffUtil
import com.mousiki.shared.domain.models.Track

class LocalSongsAdapter(
    onClickTrack: (Track) -> Unit,
    onSortClicked: () -> Unit,
    showCountsAndSortButton: Boolean
) : MousikiAdapter(
    listOf(
        LocalSongsAdapterDelegate(onClickTrack),
        HeaderSongsActionsAdapterDelegate(onSortClicked, showCountsAndSortButton)
    ),
    SongItemDiffUtil()
)