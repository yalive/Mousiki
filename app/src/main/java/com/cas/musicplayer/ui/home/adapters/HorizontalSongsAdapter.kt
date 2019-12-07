package com.cas.musicplayer.ui.home.adapters

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
 **********************************
 * Created by Abdelhadi on 4/4/19.
 **********************************
 */
class HorizontalSongsAdapter(
    private val onVideoSelected: (MusicTrack) -> Unit
) : SimpleBaseAdapter<DisplayedVideoItem, HorizontalSongViewHolder>() {

    override val cellResId: Int = R.layout.item_new_release
    override fun createViewHolder(view: View): HorizontalSongViewHolder {
        return HorizontalSongViewHolder(view, onVideoSelected, dataItems)
    }
}

class HorizontalSongViewHolder(
    view: View,
    private val onVideoSelected: (MusicTrack) -> Unit,
    private val items: List<DisplayedVideoItem>
) : SimpleBaseViewHolder<DisplayedVideoItem>(view) {

    private val imgSong: ImageView = view.findViewById(R.id.imgSong)
    private val txtTitle: TextView = view.findViewById(R.id.txtTitle)
    private val txtDuration: TextView = view.findViewById(R.id.txtDuration)

    override fun bind(item: DisplayedVideoItem) {
        imgSong.loadImage(item.songImagePath)
        txtTitle.text = item.songTitle
        txtDuration.text = item.songDuration

        itemView.findViewById<View>(R.id.cardView).setOnClickListener {
            onVideoSelected(item.track)
        }
    }
}