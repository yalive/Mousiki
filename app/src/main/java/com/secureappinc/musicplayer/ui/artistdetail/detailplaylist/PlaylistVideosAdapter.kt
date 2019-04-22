package com.secureappinc.musicplayer.ui.artistdetail.detailplaylist

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.secureappinc.musicplayer.R
import com.secureappinc.musicplayer.models.Artist
import com.secureappinc.musicplayer.models.enteties.MusicTrack
import com.secureappinc.musicplayer.player.PlayerQueue
import com.squareup.picasso.Picasso

/**
 **********************************
 * Created by Abdelhadi on 4/4/19.
 **********************************
 */
class PlaylistVideosAdapter(items: List<MusicTrack>, val artist: Artist, val onVideoSelected: () -> Unit) :
    RecyclerView.Adapter<PlaylistVideosAdapter.ViewHolder>() {

    var items: List<MusicTrack> = items
        set(value) {
            field = value
            notifyDataSetChanged()
        }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_artist, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }


    override fun getItemCount() = items.size

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        private val imgSong: ImageView = view.findViewById(R.id.imgSong)
        private val txtTitle: TextView = view.findViewById(R.id.txtTitle)
        private val txtDuration: TextView = view.findViewById(R.id.txtDuration)
        private val txtCategory: TextView = view.findViewById(R.id.txtCategory)

        init {
            view.setOnClickListener {
                onVideoSelected()
                PlayerQueue.playTrack(items[adapterPosition], items)
            }
        }

        fun bind(item: MusicTrack) {
            Picasso.get().load(item.imgUrl)
                .fit()
                .into(imgSong)
            txtTitle.text = item.title
            txtDuration.text = item.durationFormatted
            txtCategory.text = "${artist.name} - Topic"

        }
    }

}