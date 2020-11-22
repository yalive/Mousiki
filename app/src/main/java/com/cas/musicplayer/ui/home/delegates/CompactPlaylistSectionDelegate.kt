package com.cas.musicplayer.ui.home.delegates

import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.cas.common.extensions.inflate
import com.cas.delegatedadapter.AdapterDelegate
import com.cas.delegatedadapter.DisplayableItem
import com.cas.musicplayer.R
import com.cas.musicplayer.domain.model.HomeItem
import com.cas.musicplayer.ui.home.adapters.CompactPlaylistsAdapter

/**
 ***************************************
 * Created by Abdelhadi on 2019-12-04.
 ***************************************
 */
class CompactPlaylistSectionDelegate : AdapterDelegate<List<DisplayableItem>>() {

    override fun isForViewType(items: List<DisplayableItem>, position: Int): Boolean {
        return items[position] is HomeItem.CompactPlaylists
    }

    override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        val view = parent.inflate(R.layout.home_compact_playlist_section)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(
        items: List<DisplayableItem>,
        position: Int,
        holder: RecyclerView.ViewHolder
    ) {
        val section = items[position] as HomeItem.CompactPlaylists
        (holder as ViewHolder).bind(section)
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val recyclerView: RecyclerView = view.findViewById(R.id.recyclerView)
        val txtTitle: TextView = view.findViewById(R.id.txtTitle)

        fun bind(section: HomeItem.CompactPlaylists) {
            val adapter = CompactPlaylistsAdapter(section.playlists)
            recyclerView.adapter = adapter
            txtTitle.text = section.title
        }
    }
}

