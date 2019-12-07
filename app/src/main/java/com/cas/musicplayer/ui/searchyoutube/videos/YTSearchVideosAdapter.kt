package com.cas.musicplayer.ui.searchyoutube.videos

import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import com.cas.common.adapter.SimpleBaseAdapter
import com.cas.common.adapter.SimpleBaseViewHolder
import com.cas.musicplayer.R
import com.cas.musicplayer.domain.model.MusicTrack
import com.cas.musicplayer.player.PlayerQueue
import com.cas.musicplayer.ui.home.model.DisplayedVideoItem
import com.cas.musicplayer.utils.loadImage

/**
 * Created by Fayssel Yabahddou on 4/13/19.
 */
class YTSearchVideosAdapter(
    private val itemClickListener: OnItemClickListener,
    private val onVideoSelected: () -> Unit
) : SimpleBaseAdapter<DisplayedVideoItem, YTSearchVideosViewHolder>() {

    override val cellResId: Int = R.layout.item_new_release_video

    override fun createViewHolder(view: View): YTSearchVideosViewHolder {
        return YTSearchVideosViewHolder(view, dataItems, itemClickListener, onVideoSelected)
    }

    interface OnItemClickListener {
        fun onItemClick(musicTrack: MusicTrack)
    }
}

class YTSearchVideosViewHolder(
    itemView: View,
    val items: List<DisplayedVideoItem>,
    private val itemClickListener: YTSearchVideosAdapter.OnItemClickListener,
    private val onVideoSelected: () -> Unit
) : SimpleBaseViewHolder<DisplayedVideoItem>(itemView) {

    private val imgSong: ImageView = itemView.findViewById(R.id.imgSong)
    private val btnMore: ImageButton = itemView.findViewById(R.id.btnMore)
    private val txtTitle: TextView = itemView.findViewById(R.id.txtTitle)
    private val txtDuration: TextView = itemView.findViewById(R.id.txtDuration)
    private val txtCategory: TextView = itemView.findViewById(R.id.txtCategory)

    init {
        itemView.setOnClickListener {
            onVideoSelected()
            val tracks = items.map { it.track }
            PlayerQueue.playTrack(items[adapterPosition].track, tracks)
        }
    }

    override fun bind(item: DisplayedVideoItem) {
        imgSong.loadImage(item.songImagePath)
        txtTitle.text = item.songTitle
        txtDuration.text = item.songDuration
        txtCategory.text = item.songTitle.split("-")[0]
        btnMore.setOnClickListener {
            itemClickListener.onItemClick(item.track)
        }
    }
}