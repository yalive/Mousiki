package com.cas.musicplayer.ui.searchyoutube.channels

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.cas.musicplayer.R
import com.cas.musicplayer.data.enteties.Channel
import com.cas.musicplayer.data.models.Artist
import com.cas.musicplayer.ui.artists.artistdetail.ArtistFragment
import com.cas.musicplayer.utils.loadImage


/**
 **********************************
 * Created by Abdelhadi on 4/4/19.
 **********************************
 */
class YTSearchArtistsAdapter(items: List<Channel>) :
    RecyclerView.Adapter<YTSearchArtistsAdapter.ViewHolder>() {

    var items: List<Channel> = items
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(com.cas.musicplayer.R.layout.item_list_artist, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }


    override fun getItemCount() = items.size

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        private val imgSong: ImageView = view.findViewById(com.cas.musicplayer.R.id.imgSong)
        private val txtTitle: TextView = view.findViewById(com.cas.musicplayer.R.id.txtTitle)

        init {
            view.setOnClickListener {
                if (adapterPosition >= 0) {
                    val bundle = Bundle()
                    val item = items[adapterPosition]
                    val artist = Artist(item.title, "US", item.id, item.urlImage)
                    bundle.putParcelable(ArtistFragment.EXTRAS_ARTIST, artist)
                    itemView.findNavController().navigate(R.id.artistFragment, bundle)
                }
            }
        }

        fun bind(item: Channel) {
            imgSong.loadImage(item.urlImage)
            txtTitle.text = item.title
        }
    }

}