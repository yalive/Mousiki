package com.cas.musicplayer.ui.popular

import com.cas.delegatedadapter.BaseDelegationAdapter
import com.cas.musicplayer.domain.model.MusicTrack
import com.cas.musicplayer.ui.popular.delegates.LoadingDelegate
import com.cas.musicplayer.ui.popular.delegates.SongAdapterDelegate
import com.cas.musicplayer.ui.popular.delegates.SongsHeaderDelegate


/**
 * Created by Fayssel Yabahddou on 4/13/19.
 */
class PopularSongsAdapter(
    itemClickListener: SongAdapterDelegate.OnItemClickListener,
    onVideoSelected: (MusicTrack) -> Unit
) : BaseDelegationAdapter(
    listOf(
        SongAdapterDelegate(itemClickListener, onVideoSelected),
        LoadingDelegate(),
        SongsHeaderDelegate()
    )
)
