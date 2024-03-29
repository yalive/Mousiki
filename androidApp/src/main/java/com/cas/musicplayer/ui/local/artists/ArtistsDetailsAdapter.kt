package com.cas.musicplayer.ui.local.artists

import com.cas.musicplayer.delegateadapter.MousikiAdapter
import com.cas.musicplayer.ui.local.songs.HeaderSongsActionsAdapterDelegate
import com.cas.musicplayer.ui.local.songs.LocalSongsAdapterDelegate
import com.cas.musicplayer.ui.popular.SongItemDiffUtil
import com.mousiki.shared.domain.models.Track

class ArtistsDetailsAdapter(
    onClickTrack: (Track) -> Unit,
    onLongPressTrack: (Track) -> Unit
) : MousikiAdapter(
    listOf(
        LocalSongsAdapterDelegate(onClickTrack, onLongPressTrack),
        HorizontalAlbumsAdapterDelegate(),
        HeaderSongsActionsAdapterDelegate(
            onSortClicked = {},
            onFilterClicked = { },
            showCountsAndSortButton = false,
            showFilter = false
        )
    ),
    SongItemDiffUtil()
)