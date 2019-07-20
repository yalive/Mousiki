package com.cas.musicplayer.ui.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.cas.musicplayer.R
import com.cas.musicplayer.data.enteties.MusicTrack
import com.cas.musicplayer.player.PlayerQueue
import com.cas.musicplayer.utils.VideoEmplacementLiveData
import com.squareup.picasso.Picasso


/**
 **********************************
 * Created by Abdelhadi on 4/4/19.
 **********************************
 */
class HomeNewReleaseAdapter(var items: List<MusicTrack>, val onVideoSelected: () -> Unit) :
    RecyclerView.Adapter<HomeNewReleaseAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeNewReleaseAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(com.cas.musicplayer.R.layout.item_new_release, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: HomeNewReleaseAdapter.ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount() = items.size


    fun updateList(items: List<MusicTrack>) {
        this.items = items
        notifyDataSetChanged()
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        private val imgSong: ImageView = view.findViewById(com.cas.musicplayer.R.id.imgSong)
        private val txtTitle: TextView = view.findViewById(com.cas.musicplayer.R.id.txtTitle)
        private val txtDuration: TextView = view.findViewById(com.cas.musicplayer.R.id.txtDuration)

        init {

            view.findViewById<View>(R.id.cardView).setOnClickListener {
                onVideoSelected()
                PlayerQueue.playTrack(items[adapterPosition], items)
                VideoEmplacementLiveData.bottom()
            }
        }

        fun bind(item: MusicTrack) {
            Picasso.get().load(item.imgUrl)
                .fit()
                .into(imgSong)

            txtTitle.text = item.title
            txtDuration.text = formatDuration(item.duration)

        }

        private fun formatDuration(duration: String): String {
            val result = duration.replace("PT", "").replace("H", ":").replace("M", ":").replace("S", "")
            val arr = result.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()

            var timeString = ""

            if (arr.size == 1) {
                timeString = String.format(
                    "%02ds",
                    Integer.parseInt(arr[0])
                )
            } else if (arr.size == 2) {
                timeString = String.format(
                    "%02d:%02d",
                    Integer.parseInt(arr[0]),
                    Integer.parseInt(arr[1])
                )
            } else if (arr.size == 3) {
                timeString = String.format(
                    "%d:%02d:%02d",
                    Integer.parseInt(arr[0]),
                    Integer.parseInt(arr[1]),
                    Integer.parseInt(arr[2])
                )
            }

            return timeString
        }
    }
}