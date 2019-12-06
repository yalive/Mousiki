package com.cas.musicplayer.ui.favourite

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.cas.common.adapter.SimpleBaseAdapter
import com.cas.common.adapter.SimpleBaseViewHolder
import com.cas.musicplayer.R
import com.cas.musicplayer.domain.model.MusicTrack
import com.cas.musicplayer.ui.home.model.DisplayedVideoItem
import com.cas.musicplayer.utils.loadImage

/**
 * Created by Fayssel Yabahddou on 4/13/19.
 */
class FavouriteTracksAdapter(
    private val itemClickListener: OnItemClickListener
) : SimpleBaseAdapter<DisplayedVideoItem, FavouriteTracksAdapter.ViewHolder>() {

    override val cellResId: Int = R.layout.item_new_release

    override fun createViewHolder(view: View): ViewHolder {
        return ViewHolder(view)
    }

    inner class ViewHolder(itemView: View) : SimpleBaseViewHolder<DisplayedVideoItem>(itemView) {
        private val imgSong: ImageView = itemView.findViewById(R.id.imgSong)
        private val txtTitle: TextView = itemView.findViewById(R.id.txtTitle)
        private val txtDuration: TextView = itemView.findViewById(R.id.txtDuration)

        override fun bind(data: DisplayedVideoItem) {
            val track = data.track
            imgSong.loadImage(track.imgUrl)
            txtTitle.text = track.title
            txtDuration.text = track.durationFormatted
            itemView.setOnClickListener {
                itemClickListener.onSelectVideo(track)
            }
        }
    }

    interface OnItemClickListener {
        fun onItemClick(musicTrack: MusicTrack)
        fun onSelectVideo(musicTrack: MusicTrack)
    }
}