package com.cas.musicplayer.ui.library.delegates

import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.cas.common.extensions.inflate
import com.cas.common.extensions.onClick
import com.cas.musicplayer.R
import com.cas.musicplayer.delegateadapter.AdapterDelegate
import com.cas.musicplayer.ui.library.model.LibraryPlaylistItem
import com.mousiki.shared.domain.models.DisplayableItem
import com.mousiki.shared.ui.library.LibraryViewModel


/**
 **********************************
 * Created by Abdelhadi on 4/4/19.
 **********************************
 */
class LibraryCreatePlaylistDelegate(
    private val viewModel: LibraryViewModel
) : AdapterDelegate<List<DisplayableItem>>() {

    override fun isForViewType(items: List<DisplayableItem>, position: Int): Boolean {
        return items[position] is LibraryPlaylistItem.NewPlaylist
    }

    override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        val view = parent.inflate(R.layout.item_library_create_new_playlist)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(
        items: List<DisplayableItem>,
        position: Int,
        holder: RecyclerView.ViewHolder
    ) = Unit

    inner class ViewHolder(
        view: View
    ) : RecyclerView.ViewHolder(view) {
        private val txtTitle: TextView = view.findViewById(R.id.txtTitle)
        private val cardView: CardView = view.findViewById(R.id.cardView)

        init {
            cardView.onClick {
                itemView.findNavController()
                    .navigate(R.id.action_libraryFragment_to_createPlaylistFragment)
            }
        }
    }
}

