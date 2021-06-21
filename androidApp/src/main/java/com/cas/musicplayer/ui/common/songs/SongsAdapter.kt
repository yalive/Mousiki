package com.cas.musicplayer.ui.common.songs

import com.cas.musicplayer.delegateadapter.MousikiAdapter
import com.cas.musicplayer.ui.common.ads.AdsCellDelegate
import com.cas.musicplayer.ui.popular.SongItemDiffUtil
import com.cas.musicplayer.ui.popular.delegates.LoadingDelegate
import com.mousiki.shared.domain.models.Track

/**
 **********************************
 * Created by Abdelhadi on 4/4/19.
 **********************************
 */
class SongsAdapter(
    onVideoSelected: (Track) -> Unit,
    onClickMore: (Track) -> Unit
) : MousikiAdapter(
    listOf(
        SongAdapterDelegate(onClickMoreOptions = onClickMore, onVideoSelected = onVideoSelected),
        AdsCellDelegate(),
        LoadingDelegate()
    ),
    SongItemDiffUtil()
)