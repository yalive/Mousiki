package com.cas.musicplayer.ui.library.delegates

import android.view.MenuInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.PopupMenu
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.afollestad.materialdialogs.MaterialDialog
import com.cas.common.adapter.SimpleBaseViewHolder
import com.cas.common.extensions.inflate
import com.cas.common.extensions.onClick
import com.cas.musicplayer.R
import com.cas.musicplayer.delegateadapter.AdapterDelegate
import com.cas.musicplayer.ui.library.model.LibraryPlaylistItem
import com.cas.musicplayer.utils.loadImage
import com.mousiki.shared.domain.models.DisplayableItem
import com.mousiki.shared.domain.models.Playlist
import com.mousiki.shared.domain.models.isCustom
import com.mousiki.shared.domain.models.isFavourite
import com.mousiki.shared.ui.library.LibraryViewModel


/**
 **********************************
 * Created by Abdelhadi on 4/4/19.
 **********************************
 */
class LibraryCustomPlaylistDelegate(
    private val viewModel: LibraryViewModel
) : AdapterDelegate<List<DisplayableItem>>() {

    override fun isForViewType(items: List<DisplayableItem>, position: Int): Boolean {
        return items[position] is LibraryPlaylistItem.CustomPlaylist
    }

    override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        val view = parent.inflate(R.layout.item_library_playlist)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(
        items: List<DisplayableItem>,
        position: Int,
        holder: RecyclerView.ViewHolder
    ) {
        val customPlaylist = items[position] as LibraryPlaylistItem.CustomPlaylist
        (holder as ViewHolder).bind(customPlaylist.item)
    }

    inner class ViewHolder(
        view: View
    ) : SimpleBaseViewHolder<Playlist>(view) {

        private val imgSong: ImageView = view.findViewById(R.id.imgSong)
        private val txtTitle: TextView = view.findViewById(R.id.txtTitle)
        private val txtDuration: TextView = view.findViewById(R.id.txtDuration)
        private val btnMoreOptions: ImageButton = view.findViewById(R.id.btnMoreOptions)

        override fun bind(playlist: Playlist) {
            if (playlist.isFavourite) {
                txtTitle.setText(R.string.favourites)
            } else {
                txtTitle.text = playlist.title
            }

            txtDuration.text = itemView.context.resources.getQuantityString(
                R.plurals.playlist_tracks_counts,
                playlist.itemCount,
                playlist.itemCount
            )
            itemView.findViewById<View>(R.id.cardView).onClick {
                viewModel.onClickPlaylist(playlist)
            }
            btnMoreOptions.onPlaylistOption(playlist) {
                viewModel.deletePlaylist(playlist)
            }
            btnMoreOptions.isVisible = playlist.isCustom

            if (playlist.itemCount == 0) {
                imgSong.setImageResource(R.drawable.ic_music_note)
                imgSong.alpha = 0.5f
            } else {
                imgSong.loadImage(
                    urlImage = playlist.urlImage,
                    placeHolder = R.drawable.ic_music_note,
                    errorImage = R.drawable.ic_music_note,
                    onError = {
                        imgSong.alpha = 0.5f
                    }
                )
                imgSong.alpha = 1.0f
            }
        }
    }
}

inline fun View.onPlaylistOption(
    playlist: Playlist,
    crossinline onDelete: () -> Unit
) {
    onClick {
        val popup = PopupMenu(context, this)
        val inflater: MenuInflater = popup.menuInflater
        inflater.inflate(R.menu.menu_custom_playlist, popup.menu)
        popup.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.btnActionDelete -> {
                    MaterialDialog(context).show {
                        message(
                            text = context.getString(
                                R.string.confirm_delete_playlist,
                                playlist.title
                            )
                        )
                        positiveButton(res = R.string.ok) { onDelete() }
                        negativeButton(res = R.string.cancel)
                    }
                    true
                }
                else -> false
            }
        }
        popup.show()
    }
}