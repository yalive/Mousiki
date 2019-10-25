package com.cas.musicplayer.ui.bottompanel

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.cas.musicplayer.R
import com.cas.musicplayer.data.enteties.MusicTrack
import com.cas.musicplayer.player.PlayerQueue
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.item_bottom_sheet_video.view.*

/**
 **********************************
 * Created by Abdelhadi on 4/4/19.
 **********************************
 */
class BottomSheetVideosAdapter(items: List<MusicTrack>) :
    RecyclerView.Adapter<BottomSheetVideosAdapter.ViewHolder>() {

    var items: List<MusicTrack> = items
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    var onClickMore: ((track: MusicTrack) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_bottom_sheet_video, parent, false)
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
                PlayerQueue.playTrack(items[adapterPosition], items)
            }
        }

        fun bind(item: MusicTrack) {
            Picasso.get().load(item.imgUrl)
                .fit()
                .into(imgSong)
            txtTitle.text = item.title
            txtDuration.text = item.durationFormatted

            itemView.btnMore.setOnClickListener {
                onClickMore?.invoke(item)
            }
        }
    }

}