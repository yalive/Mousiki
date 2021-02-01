package com.cas.musicplayer.ui.home.delegates

import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.cas.common.extensions.inflate
import com.cas.musicplayer.delegateadapter.AdapterDelegate
import com.mousiki.shared.domain.models.DisplayableItem
import com.cas.musicplayer.R
import com.cas.musicplayer.ui.home.model.HomeItem
import com.cas.musicplayer.ui.home.adapters.CompactPlaylistsAdapter
import com.cas.musicplayer.utils.dpToPixel

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

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view), HomeMarginProvider {
        val recyclerView: RecyclerView = view.findViewById(R.id.recyclerView)
        val txtTitle: TextView = view.findViewById(R.id.txtTitle)

        fun bind(section: HomeItem.CompactPlaylists) {
            val adapter = CompactPlaylistsAdapter(section.playlists)
            recyclerView.adapter = adapter
            txtTitle.text = section.title
        }

        override fun topMargin(): Int {
            return itemView.context.dpToPixel(24f)
        }
    }
}

