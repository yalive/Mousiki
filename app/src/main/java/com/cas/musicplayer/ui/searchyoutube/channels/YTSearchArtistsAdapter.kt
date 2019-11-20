package com.cas.musicplayer.ui.searchyoutube.channels

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.navigation.findNavController
import com.cas.musicplayer.R
import com.cas.common.adapter.SimpleBaseAdapter
import com.cas.common.adapter.SimpleBaseViewHolder
import com.cas.musicplayer.domain.model.Channel
import com.cas.musicplayer.data.models.Artist
import com.cas.musicplayer.ui.artists.artistdetail.ArtistFragment
import com.cas.musicplayer.utils.loadImage


/**
 **********************************
 * Created by Abdelhadi on 4/4/19.
 **********************************
 */
class YTSearchArtistsAdapter :
    SimpleBaseAdapter<Channel, YTSearchArtistsAdapter.YTSearchArtistsViewHolder>() {
    override val cellResId: Int = R.layout.item_list_artist

    override fun createViewHolder(view: View): YTSearchArtistsViewHolder {
        return YTSearchArtistsViewHolder(view)
    }

    inner class YTSearchArtistsViewHolder(view: View) : SimpleBaseViewHolder<Channel>(view) {
        private val imgSong: ImageView = view.findViewById(R.id.imgSong)
        private val txtTitle: TextView = view.findViewById(R.id.txtTitle)

        init {
            view.setOnClickListener {
                if (adapterPosition >= 0) {
                    val bundle = Bundle()
                    val item = dataItems[adapterPosition]
                    val artist = Artist(item.title, "US", item.id, item.urlImage)
                    bundle.putParcelable(ArtistFragment.EXTRAS_ARTIST, artist)
                    itemView.findNavController().navigate(R.id.artistFragment, bundle)
                }
            }
        }

        override fun bind(item: Channel) {
            imgSong.loadImage(item.urlImage)
            txtTitle.text = item.title
        }
    }
}