package com.cas.musicplayer.ui.home.ui.adapters

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.cas.musicplayer.R
import com.cas.musicplayer.base.SimpleBaseAdapter
import com.cas.musicplayer.base.SimpleBaseViewHolder
import com.cas.musicplayer.player.PlayerQueue
import com.cas.musicplayer.ui.home.ui.model.NewReleaseDisplayedItem
import com.cas.musicplayer.utils.VideoEmplacementLiveData
import com.cas.musicplayer.utils.loadImage


/**
 **********************************
 * Created by Abdelhadi on 4/4/19.
 **********************************
 */
class HomeNewReleaseAdapter(
    private val onVideoSelected: () -> Unit
) : SimpleBaseAdapter<NewReleaseDisplayedItem, HomeNewReleaseViewHolder>() {

    override val cellResId: Int = R.layout.item_new_release
    override fun createViewHolder(view: View): HomeNewReleaseViewHolder {
        return HomeNewReleaseViewHolder(view, onVideoSelected, dataItems)
    }
}

class HomeNewReleaseViewHolder(
    view: View,
    private val onVideoSelected: () -> Unit,
    private val newReleaseItems: List<NewReleaseDisplayedItem>
) : SimpleBaseViewHolder<NewReleaseDisplayedItem>(view) {
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

    override fun bind(item: NewReleaseDisplayedItem) {
        imgSong.loadImage(item.songImagePath)
        txtTitle.text = item.songTitle
        txtDuration.text = item.songDuration
    }
}