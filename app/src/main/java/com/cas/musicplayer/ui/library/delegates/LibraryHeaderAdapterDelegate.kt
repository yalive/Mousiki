package com.cas.musicplayer.ui.library.delegates

import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.core.view.isInvisible
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.cas.common.extensions.inflate
import com.cas.delegatedadapter.AdapterDelegate
import com.cas.delegatedadapter.DisplayableItem
import com.cas.musicplayer.R
import com.cas.musicplayer.ui.library.model.LibraryHeaderItem

/**
 ***************************************
 * Created by Abdelhadi on 2019-12-04.
 ***************************************
 */
class LibraryHeaderAdapterDelegate : AdapterDelegate<List<DisplayableItem>>() {

    override fun isForViewType(items: List<DisplayableItem>, position: Int): Boolean {
        return items[position] is LibraryHeaderItem
    }

    override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        val view = parent.inflate(R.layout.item_home_header)
        return HeaderViewHolder(view)
    }

    override fun onBindViewHolder(
        items: List<DisplayableItem>,
        position: Int,
        holder: RecyclerView.ViewHolder
    ) {
        val headerItem = items[position] as LibraryHeaderItem
        (holder as HeaderViewHolder).bind(headerItem)
    }

    private inner class HeaderViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
        private val txtTitle: TextView = view.findViewById(R.id.txtTitle)
        private val showAll: ImageButton = view.findViewById(R.id.showAll)
        private val txtMore: TextView = view.findViewById(R.id.txtMore)

        fun bind(headerItem: LibraryHeaderItem) {
            txtTitle.text = headerItem.title
            if (headerItem.showMore) {
                view.setOnClickListener { showMore(headerItem) }
                showAll.setOnClickListener { showMore(headerItem) }
            }
            showAll.isInvisible = !headerItem.showMore
            txtMore.isInvisible = !headerItem.showMore
        }

        private fun showMore(headerItem: LibraryHeaderItem) {
            val destination = when (headerItem) {
                LibraryHeaderItem.RecentHeader -> -1
                LibraryHeaderItem.FavouriteHeader -> R.id.action_libraryFragment_to_favouriteSongsFragment
                LibraryHeaderItem.HeavyHeader -> -1
            }
            if (destination != -1) {
                itemView.findNavController().navigate(destination)
            }
        }
    }
}