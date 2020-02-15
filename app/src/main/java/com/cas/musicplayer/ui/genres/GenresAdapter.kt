package com.cas.musicplayer.ui.genres

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.navigation.findNavController
import com.cas.common.adapter.SimpleBaseAdapter
import com.cas.common.adapter.SimpleBaseViewHolder
import com.cas.musicplayer.R
import com.cas.musicplayer.data.remote.models.Artist
import com.cas.musicplayer.domain.model.GenreMusic
import com.cas.musicplayer.ui.artists.EXTRAS_ARTIST
import com.cas.musicplayer.ui.playlistvideos.PlaylistSongsFragment
import com.cas.musicplayer.utils.dpToPixel
import com.cas.musicplayer.utils.drawable

/**
 * Created by Fayssel Yabahddou on 4/13/19.
 */
class GenresAdapter : SimpleBaseAdapter<GenreMusic, GenresViewHolder>() {
    override val cellResId: Int = R.layout.item_genre
    override fun createViewHolder(view: View) = GenresViewHolder(view)
}

class GenresViewHolder(itemView: View) : SimpleBaseViewHolder<GenreMusic>(itemView) {

    private val imgCategory: ImageView = itemView.findViewById(R.id.imgCategory)
    private val txtTitle: TextView = itemView.findViewById(R.id.txtTitle)
    private val cardView: View = itemView.findViewById(R.id.cardView)

    override fun bind(item: GenreMusic) {
        itemView.layoutParams = when (adapterPosition) {
            0 -> ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            else -> ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                itemView.context.dpToPixel(120f)
            )
        }

        imgCategory.setImageDrawable(itemView.context.drawable(item.img))
        txtTitle.text = item.title
        cardView.setOnClickListener {
            val bundle = Bundle()
            bundle.putString(PlaylistSongsFragment.EXTRAS_PLAYLIST_ID, item.topTracksPlaylist)
            val artist = Artist(item.title, "US", item.topTracksPlaylist)
            bundle.putParcelable(EXTRAS_ARTIST, artist)
            itemView.findNavController().navigate(R.id.playlistVideosFragment, bundle)
        }
    }
}