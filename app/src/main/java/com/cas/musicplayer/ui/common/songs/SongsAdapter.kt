package com.cas.musicplayer.ui.common.songs

import com.cas.delegatedadapter.BaseDelegationAdapter
import com.cas.musicplayer.domain.model.MusicTrack

/**
 **********************************
 * Created by Abdelhadi on 4/4/19.
 **********************************
 */
class SongsAdapter(
    onVideoSelected: (MusicTrack) -> Unit,
    onClickMore: (MusicTrack) -> Unit
) : BaseDelegationAdapter(
    listOf(
        SongAdapterDelegate(onClickMoreOptions = onClickMore, onVideoSelected = onVideoSelected),
        EmptyCellDelegate()
    )
)