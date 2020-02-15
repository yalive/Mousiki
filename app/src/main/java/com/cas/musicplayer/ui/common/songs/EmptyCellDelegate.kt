package com.cas.musicplayer.ui.common.songs

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.cas.common.extensions.inflate
import com.cas.delegatedadapter.AdapterDelegate
import com.cas.delegatedadapter.DisplayableItem
import com.cas.musicplayer.R

/**
 ***************************************
 * Created by Abdelhadi on 2019-12-01.
 ***************************************
 */
class EmptyCellDelegate : AdapterDelegate<List<DisplayableItem>>() {

    override fun isForViewType(items: List<DisplayableItem>, position: Int): Boolean {
        return items[position] is EmptyCellItem
    }

    override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        val view = parent.inflate(R.layout.item_empty_cell)
        return PopularSongsViewHolder(view)
    }

    override fun onBindViewHolder(
        items: List<DisplayableItem>,
        position: Int,
        holder: RecyclerView.ViewHolder
    ) {
    }

    inner class PopularSongsViewHolder(
        itemView: View
    ) : RecyclerView.ViewHolder(itemView)
}

object EmptyCellItem : DisplayableItem