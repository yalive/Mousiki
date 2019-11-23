package com.cas.musicplayer.ui.playlistvideos

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.cas.musicplayer.R
import com.cas.common.adapter.SimpleBaseAdapter
import com.cas.common.adapter.SimpleBaseViewHolder
import com.cas.musicplayer.domain.model.MusicTrack
import com.cas.musicplayer.data.remote.models.Artist
import com.cas.musicplayer.player.PlayerQueue
import com.cas.musicplayer.ui.home.model.DisplayedVideoItem
import com.cas.musicplayer.utils.loadImage
import kotlinx.android.synthetic.main.item_artist.view.*

/**
 **********************************
 * Created by Abdelhadi on 4/4/19.
 **********************************
 */
class PlaylistVideosAdapter(
    private val artist: Artist,
    private val onVideoSelected: () -> Unit,
    private val onClickMore: ((track: MusicTrack) -> Unit)
) : SimpleBaseAdapter<DisplayedVideoItem, PlaylistVideosViewHolder>() {
    override val cellResId: Int = R.layout.item_artist
    override fun createViewHolder(view: View): PlaylistVideosViewHolder {
        return PlaylistVideosViewHolder(view, dataItems, artist, onVideoSelected, onClickMore)
    }
}

class PlaylistVideosViewHolder(
    view: View,
    private val items: List<DisplayedVideoItem>,
    private val artist: Artist,
    private val onVideoSelected: () -> Unit,
    private val onClickMore: ((track: MusicTrack) -> Unit)
) : SimpleBaseViewHolder<DisplayedVideoItem>(view) {

    private val imgSong: ImageView = view.findViewById(R.id.imgSong)
    private val txtTitle: TextView = view.findViewById(R.id.txtTitle)
    private val txtDuration: TextView = view.findViewById(R.id.txtDuration)
    private val txtCategory: TextView = view.findViewById(R.id.txtCategory)

    init {
        view.setOnClickListener {
            onVideoSelected()
            val tracks = items.map { it.track }
            PlayerQueue.playTrack(items[adapterPosition].track, tracks)
        }
    }

    override fun bind(item: DisplayedVideoItem) {
        imgSong.loadImage(item.songImagePath)
        txtTitle.text = item.songTitle
        txtDuration.text = item.songDuration
        txtCategory.text = "${artist.name} - Topic"

        itemView.btnMore.setOnClickListener {
            onClickMore.invoke(item.track)
        }
    }
}