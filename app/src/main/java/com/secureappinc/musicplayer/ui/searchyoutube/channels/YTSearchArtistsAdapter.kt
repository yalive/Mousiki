package com.secureappinc.musicplayer.ui.searchyoutube.channels

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.secureappinc.musicplayer.R
import com.secureappinc.musicplayer.models.Artist
import com.secureappinc.musicplayer.ui.artistdetail.ArtistFragment
import com.squareup.picasso.Picasso


/**
 **********************************
 * Created by Abdelhadi on 4/4/19.
 **********************************
 */
class YTSearchArtistsAdapter(items: List<Artist>) :
    RecyclerView.Adapter<YTSearchArtistsAdapter.ViewHolder>() {

    var items: List<Artist> = items
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(com.secureappinc.musicplayer.R.layout.item_list_artist, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }


    override fun getItemCount() = items.size

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        private val imgSong: ImageView = view.findViewById(com.secureappinc.musicplayer.R.id.imgSong)
        private val txtTitle: TextView = view.findViewById(com.secureappinc.musicplayer.R.id.txtTitle)

        init {
            view.setOnClickListener {
                if (adapterPosition > 0) {
                    //onClickArtist(items[adapterPosition])
                    val bundle = Bundle()
                    bundle.putParcelable(ArtistFragment.EXTRAS_ARTIST, items[adapterPosition])
                    itemView.findNavController().navigate(R.id.artistFragment, bundle)
                }
            }
        }

        fun bind(item: Artist) {
            item.urlImage?.let { urlImage ->
                if (urlImage.isNotEmpty()) {
                    Picasso.get().load(urlImage)
                        .fit()
                        .into(imgSong)
                }
            }

            txtTitle.text = item.name
        }
    }

}