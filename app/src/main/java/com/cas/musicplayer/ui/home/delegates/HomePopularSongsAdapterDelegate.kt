package com.cas.musicplayer.ui.home.delegates

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.cas.common.extensions.inflate
import com.cas.delegatedadapter.AdapterDelegate
import com.cas.delegatedadapter.DisplayableItem
import com.cas.musicplayer.R
import com.cas.musicplayer.domain.model.HomeItem
import com.cas.musicplayer.ui.home.adapters.HomePopularSongsAdapter
import com.cas.musicplayer.ui.home.model.DisplayedVideoItem

/**
 ***************************************
 * Created by Abdelhadi on 2019-12-04.
 ***************************************
 */

class HomePopularSongsAdapterDelegate(
    private val onVideoSelected: () -> Unit
) : AdapterDelegate<List<DisplayableItem>>() {

    override fun isForViewType(items: List<DisplayableItem>, position: Int): Boolean {
        return items[position] is HomeItem.PopularsItem
    }

    override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        val view = parent.inflate(R.layout.item_home_new_release)
        return PopularSongsViewHolder(view)
    }

    override fun onBindViewHolder(items: List<DisplayableItem>, position: Int, holder: RecyclerView.ViewHolder) {
        val popularItem = items[position] as HomeItem.PopularsItem
        (holder as PopularSongsViewHolder).update(popularItem.songs)
    }

    private inner class PopularSongsViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private var adapter = HomePopularSongsAdapter(onVideoSelected)

        init {
            val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView)
            recyclerView.adapter = adapter
        }

        fun update(songs: List<DisplayedVideoItem>) {
            adapter.dataItems = songs.toMutableList()
        }
    }
}
