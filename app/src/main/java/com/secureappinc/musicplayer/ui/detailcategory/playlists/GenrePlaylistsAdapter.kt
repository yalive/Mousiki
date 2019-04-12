package com.secureappinc.musicplayer.ui.detailcategory.playlists

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.secureappinc.musicplayer.R
import com.secureappinc.musicplayer.models.YTTrendingItem
import com.secureappinc.musicplayer.ui.home.models.GenreMusic
import com.squareup.picasso.Picasso

/**
 **********************************
 * Created by Abdelhadi on 4/4/19.
 **********************************
 */
class GenrePlaylistsAdapter(items: List<YTTrendingItem>, val genreMusic: GenreMusic) : RecyclerView.Adapter<GenrePlaylistsAdapter.ViewHolder>() {

    var items: List<YTTrendingItem> = items
        set(value) {
            field = value
            notifyDataSetChanged()
        }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_channel_playlist, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }


    override fun getItemCount() = items.size

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        private val imgSong: ImageView = view.findViewById(R.id.imgSong)
        private val txtTitle: TextView = view.findViewById(R.id.txtTitle)
        private val txtCategory: TextView = view.findViewById(R.id.txtCategory)
        private val txtCount: TextView = view.findViewById(R.id.txtCount)

        fun bind(item: YTTrendingItem) {
            Picasso.get().load(item.snippet.thumbnails.high.url)
                    .fit()
                    .into(imgSong)
            txtTitle.text = item.snippet.title
            txtCategory.text = "${genreMusic.title} - Topic"
            txtCount.text = "${item.contentDetails.itemCount}"
        }
    }

}