package com.cas.musicplayer.ui.popular.delegates

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.cas.common.extensions.inflate
import com.cas.musicplayer.R
import com.cas.delegatedadapter.AdapterDelegate
import com.cas.delegatedadapter.DisplayableItem
import com.cas.musicplayer.ui.popular.model.SongsHeaderItem

/**
 ***************************************
 * Created by Abdelhadi on 2019-12-03.
 ***************************************
 */

class SongsHeaderDelegate : AdapterDelegate<List<DisplayableItem>>() {

    override fun isForViewType(items: List<DisplayableItem>, position: Int): Boolean {
        return items[position] is SongsHeaderItem
    }

    override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        val view = parent.inflate(R.layout.item_songs_header)
        return SongsHeaderViewHolder(view)
    }

    override fun onBindViewHolder(items: List<DisplayableItem>, position: Int, holder: RecyclerView.ViewHolder) {
    }

    inner class SongsHeaderViewHolder(
        itemView: View
    ) : RecyclerView.ViewHolder(itemView)
}

