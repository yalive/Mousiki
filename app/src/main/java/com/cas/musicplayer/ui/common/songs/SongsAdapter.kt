package com.cas.musicplayer.ui.common.songs

import com.cas.delegatedadapter.BaseDelegationAdapter
import com.cas.musicplayer.domain.model.MusicTrack
import com.cas.musicplayer.ui.common.ads.AdsCellDelegate
import com.cas.musicplayer.ui.popular.delegates.LoadingDelegate

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
        AdsCellDelegate(),
        EmptyCellDelegate(),
        LoadingDelegate()
    )
)