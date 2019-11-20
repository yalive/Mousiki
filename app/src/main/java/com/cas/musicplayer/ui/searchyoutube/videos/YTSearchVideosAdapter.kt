package com.cas.musicplayer.ui.searchyoutube.videos

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.cas.musicplayer.R
import com.cas.musicplayer.data.enteties.MusicTrack
import com.cas.musicplayer.player.PlayerQueue
import com.squareup.picasso.Picasso

/**
 * Created by Fayssel Yabahddou on 4/13/19.
 */
class YTSearchVideosAdapter(
    items: List<MusicTrack>,
    private val itemClickListener: OnItemClickListener,
    val onVideoSelected: () -> Unit
) :
    RecyclerView.Adapter<YTSearchVideosAdapter.YTSearchVideosViewHoder>() {

    var items: List<MusicTrack> = items
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): YTSearchVideosViewHoder {

        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_new_release_video, parent, false)
        return YTSearchVideosViewHoder(view)
    }

    override fun getItemCount(): Int {

        return items.size
    }

    override fun onBindViewHolder(holder: YTSearchVideosViewHoder, position: Int) {
        holder.bind(items.get(position), itemClickListener)
    }


    inner class YTSearchVideosViewHoder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val imgSong: ImageView = itemView.findViewById(R.id.imgSong)
        private val btnMore: ImageButton = itemView.findViewById(R.id.btnMore)
        private val txtTitle: TextView = itemView.findViewById(R.id.txtTitle)
        private val txtDuration: TextView = itemView.findViewById(R.id.txtDuration)
        private val txtCategory: TextView = itemView.findViewById(R.id.txtCategory)

        init {
            itemView.setOnClickListener {
                onVideoSelected()
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
    }
}