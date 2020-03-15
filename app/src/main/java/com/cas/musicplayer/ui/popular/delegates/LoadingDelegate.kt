package com.cas.musicplayer.ui.popular.delegates

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.cas.common.extensions.inflate
import com.cas.delegatedadapter.AdapterDelegate
import com.cas.delegatedadapter.DisplayableItem
import com.cas.delegatedadapter.LoadingItem
import com.cas.musicplayer.R

/**
 ***************************************
 * Created by Abdelhadi on 2019-12-01.
 ***************************************
 */
class LoadingDelegate : AdapterDelegate<List<DisplayableItem>>() {

    override fun isForViewType(items: List<DisplayableItem>, position: Int): Boolean {
        return items[position] is LoadingItem
    }

    override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        val view = parent.inflate(R.layout.item_loading_vertial_list)
        return LoadingViewHolder(view)
    }

    override fun onBindViewHolder(
        items: List<DisplayableItem>,
        position: Int,
        holder: RecyclerView.ViewHolder
    ) {
    }

    inner class LoadingViewHolder(
        itemView: View
    ) : RecyclerView.ViewHolder(itemView)
}



