package com.cas.musicplayer.ui.library.adapters

import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import com.cas.common.adapter.SimpleBaseAdapter
import com.cas.common.adapter.SimpleBaseViewHolder
import com.cas.common.extensions.onClick
import com.cas.musicplayer.R
import com.cas.musicplayer.domain.model.Playlist
import com.cas.musicplayer.ui.library.LibraryViewModel
import com.cas.musicplayer.utils.loadImage


/**
 **********************************
 * Created by Abdelhadi on 4/4/19.
 **********************************
 */
class LibraryPlaylistsAdapter(
    private val viewModel: LibraryViewModel
) : SimpleBaseAdapter<Playlist, LibraryPlaylistsAdapter.ViewHolder>() {

    override val cellResId: Int = R.layout.item_library_playlist
    override fun createViewHolder(view: View): ViewHolder {
        return ViewHolder(view, viewModel)
    }

    inner class ViewHolder(
        view: View,
        private val libraryViewModel: LibraryViewModel
    ) : SimpleBaseViewHolder<Playlist>(view) {

        private val imgSong: ImageView = view.findViewById(R.id.imgSong)
        private val txtTitle: TextView = view.findViewById(R.id.txtTitle)
        private val txtDuration: TextView = view.findViewById(R.id.txtDuration)
        private val btnMoreOptions: ImageButton = view.findViewById(R.id.btnMoreOptions)

        override fun bind(playlist: Playlist) {
            imgSong.loadImage(playlist.urlImage)
            txtTitle.text = playlist.title
            txtDuration.text = itemView.context.resources.getQuantityString(
                R.plurals.playlist_tracks_counts,
                playlist.itemCount,
                playlist.itemCount
            )

            itemView.findViewById<View>(R.id.cardView).setOnClickListener {
                viewModel.onClickPlaylist(playlist)
            }
            btnMoreOptions.onClick {

            }
        }
    }
}

