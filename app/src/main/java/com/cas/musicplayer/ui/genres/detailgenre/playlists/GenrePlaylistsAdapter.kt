package com.cas.musicplayer.ui.genres.detailgenre.playlists

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.cas.musicplayer.R
import com.cas.musicplayer.data.enteties.Playlist
import com.cas.musicplayer.ui.home.domain.model.GenreMusic
import com.cas.musicplayer.utils.loadImage

/**
 **********************************
 * Created by Abdelhadi on 4/4/19.
 **********************************
 */
class GenrePlaylistsAdapter(items: List<Playlist>, val genreMusic: GenreMusic) :
    RecyclerView.Adapter<GenrePlaylistsAdapter.ViewHolder>() {

    var items: List<Playlist> = items
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

        fun bind(item: Playlist) {
            imgSong.loadImage(item.urlImage)
            txtTitle.text = item.title
            txtCategory.text = "${genreMusic.title}"
            txtCount.text = "${item.itemCount}"
        }
    }
}
