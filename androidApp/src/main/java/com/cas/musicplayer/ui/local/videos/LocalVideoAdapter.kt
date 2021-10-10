package com.cas.musicplayer.ui.local.videos

import com.cas.musicplayer.delegateadapter.MousikiAdapter
import com.cas.musicplayer.ui.common.ads.AdsCellDelegate
import com.cas.musicplayer.ui.popular.SongItemDiffUtil
import com.mousiki.shared.domain.models.Track

class LocalVideoAdapter(
    onClickTrack: (Track) -> Unit,
    onSortClicked: () -> Unit,
    onFilterClicked: () -> Unit,
    showCountsAndSortButton: Boolean,
    showFilter: Boolean,
    isFromHistory: Boolean = false
) : MousikiAdapter(
    listOf(
        LocalVideoAdapterDelegate(isFromHistory, onClickTrack),
        HeaderVideoActionsAdapterDelegate(
            onSortClicked = onSortClicked,
            onFilterClicked = onFilterClicked,
            showCountsAndSortButton = showCountsAndSortButton,
            showFilter = showFilter
        ),
        AdsCellDelegate()
    ),
    SongItemDiffUtil()
)