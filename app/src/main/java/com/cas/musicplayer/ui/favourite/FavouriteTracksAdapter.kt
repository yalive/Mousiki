package com.cas.musicplayer.ui.favourite

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.cas.musicplayer.R
import com.cas.musicplayer.domain.model.MusicTrack
import com.cas.musicplayer.player.PlayerQueue
import com.squareup.picasso.Picasso

/**
 * Created by Fayssel Yabahddou on 4/13/19.
 */
class FavouriteTracksAdapter(items: List<MusicTrack>, private val itemClickListener: OnItemClickListener) :
    RecyclerView.Adapter<FavouriteTracksAdapter.PlayListViewHolder>() {

    var items: List<MusicTrack> = items
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlayListViewHolder {

        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_fav_video, parent, false)
        return PlayListViewHolder(view)
    }

    override fun getItemCount(): Int {

        return items.size
    }

    override fun onBindViewHolder(holder: PlayListViewHolder, position: Int) {
        holder.bind(items.get(position), itemClickListener)
    }


    inner class PlayListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val imgSong: ImageView = itemView.findViewById(R.id.imgSong)
        private val btnMore: ImageButton = itemView.findViewById(R.id.btnMore)
        private val txtTitle: TextView = itemView.findViewById(R.id.txtTitle)
        private val txtDuration: TextView = itemView.findViewById(R.id.txtDuration)
        private val txtCategory: TextView = itemView.findViewById(R.id.txtCategory)

        init {
            itemView.setOnClickListener {
                itemClickListener.onSelectVideo(items[adapterPosition])
                PlayerQueue.playTrack(items[adapterPosition], items)
            }
        }

        fun bind(item: MusicTrack, itemClickListener: OnItemClickListener) {
            Picasso.get().load(item.imgUrl)
                .fit()
                .into(imgSong)
            txtTitle.text = item.title
            txtDuration.text = item.durationFormatted
            txtCategory.text = item.title.split("-")[0]
            btnMore.setOnClickListener {
                itemClickListener.onItemClick(item)
            }

        }
    }

    interface OnItemClickListener {
        fun onItemClick(musicTrack: MusicTrack)
        fun onSelectVideo(musicTrack: MusicTrack)
    }
}