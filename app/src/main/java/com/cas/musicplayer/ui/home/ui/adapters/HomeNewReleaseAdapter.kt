package com.cas.musicplayer.ui.home.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.cas.musicplayer.R
import com.cas.musicplayer.player.PlayerQueue
import com.cas.musicplayer.ui.home.ui.model.NewReleaseDisplayedItem
import com.cas.musicplayer.utils.VideoEmplacementLiveData
import com.cas.musicplayer.utils.loadImage
import com.cas.musicplayer.utils.observer


/**
 **********************************
 * Created by Abdelhadi on 4/4/19.
 **********************************
 */
class HomeNewReleaseAdapter(
    val onVideoSelected: () -> Unit
) : RecyclerView.Adapter<HomeNewReleaseAdapter.ViewHolder>() {

    var newReleaseItems: List<NewReleaseDisplayedItem> by observer(emptyList()) {
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_new_release, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(newReleaseItems[position])
    }

    override fun getItemCount() = newReleaseItems.size

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val imgSong: ImageView = view.findViewById(R.id.imgSong)
        private val txtTitle: TextView = view.findViewById(R.id.txtTitle)
        private val txtDuration: TextView = view.findViewById(R.id.txtDuration)

        init {
            view.findViewById<View>(R.id.cardView).setOnClickListener {
                onVideoSelected()
                val tracks = newReleaseItems.map { it.track }
                PlayerQueue.playTrack(newReleaseItems[adapterPosition].track, tracks)
                VideoEmplacementLiveData.bottom()
            }
        }

        fun bind(item: NewReleaseDisplayedItem) {
            imgSong.loadImage(item.songImagePath)
            txtTitle.text = item.songTitle
            txtDuration.text = item.songDuration
        }
    }
}