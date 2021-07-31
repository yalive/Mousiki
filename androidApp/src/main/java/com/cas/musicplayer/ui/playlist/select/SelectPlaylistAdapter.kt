package com.cas.musicplayer.ui.playlist.create

import android.content.Context
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.cas.common.adapter.SimpleBaseAdapter
import com.cas.common.adapter.SimpleBaseViewHolder
import com.cas.common.extensions.onClick
import com.cas.musicplayer.R
import com.mousiki.shared.domain.models.Playlist
import com.mousiki.shared.domain.models.isCustom
import com.mousiki.shared.domain.models.isFavourite
import com.squareup.picasso.Picasso

/**
 ***************************************
 * Created by Y.Abdelhadi on 4/4/20.
 ***************************************
 */
class SelectPlaylistAdapter(
    private val context: Context,
    private val onSelectPlaylist: (Playlist) -> Unit
) : SimpleBaseAdapter<Playlist, SelectPlaylistAdapter.ViewHolder>() {

    override val cellResId: Int = R.layout.item_select_custom_playlist

    override fun createViewHolder(view: View): ViewHolder {
        return ViewHolder(view)
    }

    inner class ViewHolder(view: View) : SimpleBaseViewHolder<Playlist>(view) {
        private val txtTitle = view.findViewById<TextView>(R.id.txtTitle)
        private val txtTracksCount = view.findViewById<TextView>(R.id.txtTracksCount)
        private val imgPlaylist = view.findViewById<ImageView>(R.id.imgPlaylist)

        init {
            itemView.onClick {
                val playlist = dataItems.getOrNull(adapterPosition) ?: return@onClick
                onSelectPlaylist(playlist)
            }
        }

        override fun bind(playlist: Playlist) {
            imgPlaylist.scaleType =
                if (playlist.isCustom) ImageView.ScaleType.CENTER_CROP
                else ImageView.ScaleType.CENTER
            if (playlist.isFavourite) {
                txtTitle.setText(R.string.favourites)
            } else {
                txtTitle.text = playlist.title
            }
            txtTracksCount.text = context.resources.getQuantityString(
                R.plurals.playlist_tracks_counts,
                playlist.itemCount,
                playlist.itemCount
            )
            val drawable = when (playlist.type) {
                Playlist.TYPE_FAV -> R.drawable.fav_playlist
                Playlist.TYPE_HEAVY -> R.drawable.most_played_playlist
                Playlist.TYPE_RECENT -> R.drawable.recently_played_playlist
                else -> R.drawable.playlist_placeholder_image
            }

            val urlImage = if (playlist.urlImage.isNotEmpty()) playlist.urlImage else null
            Picasso.get()
                .load(urlImage)
                .placeholder(drawable)
                .into(imgPlaylist)
        }
    }
}