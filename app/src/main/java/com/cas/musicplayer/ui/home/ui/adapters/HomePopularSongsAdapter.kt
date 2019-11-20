package com.cas.musicplayer.ui.home.ui.adapters

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.cas.musicplayer.R
import com.cas.musicplayer.base.SimpleBaseAdapter
import com.cas.musicplayer.base.SimpleBaseViewHolder
import com.cas.musicplayer.player.PlayerQueue
import com.cas.musicplayer.ui.home.ui.model.DisplayedVideoItem
import com.cas.musicplayer.utils.VideoEmplacementLiveData
import com.cas.musicplayer.utils.loadImage


/**
 **********************************
 * Created by Abdelhadi on 4/4/19.
 **********************************
 */
class HomePopularSongsAdapter(
    private val onVideoSelected: () -> Unit
) : SimpleBaseAdapter<DisplayedVideoItem, HomePopularSongsViewHolder>() {

    override val cellResId: Int = R.layout.item_new_release
    override fun createViewHolder(view: View): HomePopularSongsViewHolder {
        return HomePopularSongsViewHolder(view, onVideoSelected, dataItems)
    }
}

class HomePopularSongsViewHolder(
    view: View,
    private val onVideoSelected: () -> Unit,
    private val newReleaseItems: List<DisplayedVideoItem>
) : SimpleBaseViewHolder<DisplayedVideoItem>(view) {
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

    override fun bind(item: DisplayedVideoItem) {
        imgSong.loadImage(item.songImagePath)
        txtTitle.text = item.songTitle
        txtDuration.text = item.songDuration
    }
}