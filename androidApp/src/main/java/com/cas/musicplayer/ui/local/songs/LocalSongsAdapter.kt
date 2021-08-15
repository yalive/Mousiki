package com.cas.musicplayer.ui.local.songs

import android.util.Log
import com.cas.musicplayer.delegateadapter.MousikiAdapter
import com.cas.musicplayer.ui.popular.SongItemDiffUtil
import com.mousiki.shared.domain.models.DisplayableItem
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
) {
    override fun submitList(newList: List<DisplayableItem>, callback: () -> Unit) {
        Log.d("LocalSongsAdapter", "submitList called")
        Log.d("LocalSongsAdapter", "recyclerView ${recyclerView != null}")
        Log.d("LocalSongsAdapter", "newList ${newList.size}")
        val recyclerViewState = recyclerView?.layoutManager?.onSaveInstanceState()
        differ.submitList(newList, callback)
        recyclerView?.post {
            recyclerView?.layoutManager?.onRestoreInstanceState(recyclerViewState)
        }
    }
}