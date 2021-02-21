package com.cas.musicplayer.ui.library.delegates

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.cas.common.extensions.inflate
import com.cas.musicplayer.delegateadapter.AdapterDelegate
import com.mousiki.shared.domain.models.DisplayableItem
import com.cas.musicplayer.R
import com.mousiki.shared.ui.library.LibraryViewModel
import com.cas.musicplayer.ui.library.adapters.LibraryPlaylistsAdapter
import com.cas.musicplayer.ui.library.model.LibraryItem
import com.cas.musicplayer.ui.library.model.LibraryPlaylistItem

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
        (holder as ViewHolder).bind(itemPlaylists.items)
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private var adapter =
            LibraryPlaylistsAdapter(
                viewModel
            )
        private val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView)

        init {
            recyclerView.adapter = adapter
        }

        fun bind(items: List<LibraryPlaylistItem>) {
            adapter.dataItems = items.toMutableList()
        }
    }
}

