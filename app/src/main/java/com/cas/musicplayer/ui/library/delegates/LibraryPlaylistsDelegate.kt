package com.cas.musicplayer.ui.library.delegates

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.cas.common.extensions.inflate
import com.cas.delegatedadapter.AdapterDelegate
import com.cas.delegatedadapter.DisplayableItem
import com.cas.musicplayer.R
import com.cas.musicplayer.domain.model.Playlist
import com.cas.musicplayer.ui.library.LibraryViewModel
import com.cas.musicplayer.ui.library.adapters.LibraryPlaylistsAdapter
import com.cas.musicplayer.ui.library.model.LibraryItem

/**
 ***************************************
 * Created by Y.Abdelhadi on 4/5/20.
 ***************************************
 */
class LibraryPlaylistsDelegate(
    private val viewModel: LibraryViewModel
) : AdapterDelegate<List<DisplayableItem>>() {

    override fun isForViewType(items: List<DisplayableItem>, position: Int): Boolean {
        return items[position] is LibraryItem.Playlists
    }

    override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        val view = parent.inflate(R.layout.library_playlists)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(
        items: List<DisplayableItem>,
        position: Int,
        holder: RecyclerView.ViewHolder
    ) {
        val itemPlaylists = items[position] as LibraryItem.Playlists
        (holder as ViewHolder).bind(itemPlaylists.playlists)
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private var adapter = LibraryPlaylistsAdapter(viewModel)
        private val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView)

        init {
            recyclerView.adapter = adapter
        }

        fun bind(playlists: List<Playlist>) {
            adapter.dataItems = playlists.toMutableList()
        }
    }
}