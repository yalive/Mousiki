package com.cas.musicplayer.ui.local.songs

import com.cas.musicplayer.delegateadapter.MousikiAdapter
import com.cas.musicplayer.ui.common.ads.AdsCellDelegateForLocal
import com.cas.musicplayer.ui.popular.SongItemDiffUtil
import com.mousiki.shared.domain.models.Track

class LocalSongsAdapter(
    onClickTrack: (Track) -> Unit,
    onLongPressTrack: (Track) -> Unit,
    onSortClicked: () -> Unit,
    onFilterClicked: () -> Unit,
    showCountsAndSortButton: Boolean,
    showFilter: Boolean
) : MousikiAdapter(
    listOf(
        LocalSongsAdapterDelegate(onClickTrack, onLongPressTrack),
        HeaderSongsActionsAdapterDelegate(
            onSortClicked = onSortClicked,
            onFilterClicked = onFilterClicked,
            showCountsAndSortButton = showCountsAndSortButton,
            showFilter = showFilter
        ),
        AdsCellDelegateForLocal()
    ),
    SongItemDiffUtil()
)