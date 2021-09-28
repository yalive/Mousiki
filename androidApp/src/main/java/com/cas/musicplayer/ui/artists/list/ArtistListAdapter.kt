package com.cas.musicplayer.ui.artists.list

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.cas.common.adapter.SimpleBaseAdapter
import com.cas.common.adapter.SimpleBaseViewHolder
import com.cas.common.extensions.onClick
import com.cas.musicplayer.R
import com.cas.musicplayer.ui.artists.thumbnail
import com.mousiki.shared.data.models.Artist
import com.squareup.picasso.Picasso


/**
 **********************************
 * Created by Abdelhadi on 4/4/19.
 **********************************
 */
class ArtistListAdapter(
    private val onClickArtist: (artist: Artist) -> Unit
) : SimpleBaseAdapter<Artist, ArtistListAdapter.ArtistsViewHolder>() {

    override val cellResId: Int = R.layout.item_list_artist

    override fun createViewHolder(view: View): ArtistsViewHolder {
        return ArtistsViewHolder(view, onClickArtist)
    }

    override fun getItemViewType(position: Int): Int {
        return R.layout.item_list_artist
    }

    inner class ArtistsViewHolder(
        view: View,
        var onClickArtist: (artist: Artist) -> Unit
    ) : SimpleBaseViewHolder<Artist>(view) {

        private val imgSong: ImageView = view.findViewById(R.id.imgSong)
        private val txtTitle: TextView = view.findViewById(R.id.txtTitle)

        init {
            itemView.onClick {
                onClickArtist(itemView.tag as Artist)
            }
        }

        override fun bind(artist: Artist) {
            itemView.tag = artist
            Picasso.get().load(artist.thumbnail).into(imgSong)
            txtTitle.text = artist.name

        }
    }
}