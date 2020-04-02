package com.cas.musicplayer.ui.genres

import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.navigation.findNavController
import com.cas.common.adapter.SimpleBaseAdapter
import com.cas.common.adapter.SimpleBaseViewHolder
import com.cas.musicplayer.R
import com.cas.musicplayer.data.remote.models.Artist
import com.cas.musicplayer.domain.model.GenreMusic
import com.cas.musicplayer.ui.artists.EXTRAS_ARTIST
import com.cas.musicplayer.ui.common.songs.BaseSongsFragment
import com.cas.musicplayer.ui.common.songs.FeaturedImage
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
            val artist = Artist(item.title, "US", item.topTracksPlaylist)
            val bundle = bundleOf(
                PlaylistSongsFragment.EXTRAS_PLAYLIST_ID to item.topTracksPlaylist,
                EXTRAS_ARTIST to artist,
                BaseSongsFragment.EXTRAS_ID_FEATURED_IMAGE to FeaturedImage.FeaturedImageRes(item.img)
            )
            itemView.findNavController()
                .navigate(R.id.action_genresFragment_to_playlistVideosFragment, bundle)
        }
    }
}