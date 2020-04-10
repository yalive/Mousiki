package com.cas.musicplayer.ui.home.adapters

import androidx.recyclerview.widget.DiffUtil
import com.cas.musicplayer.domain.model.ChartModel

class ChartsDiffUtil(
    private val oldList: List<ChartModel>,
    private val newList: List<ChartModel>
) : DiffUtil.Callback() {

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldItem = oldList[oldItemPosition]
        val newItem = newList[newItemPosition]
        return oldItem.playlistId == newItem.playlistId
    }

    override fun getOldListSize(): Int = oldList.size

    override fun getNewListSize(): Int = newList.size

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldItem = oldList[oldItemPosition]
        val newItem = newList[newItemPosition]
        return oldItem.playlistId == newItem.playlistId
                && oldItem.title == newItem.title
                && oldItem.featuredImageRes == newItem.featuredImageRes
    }
}