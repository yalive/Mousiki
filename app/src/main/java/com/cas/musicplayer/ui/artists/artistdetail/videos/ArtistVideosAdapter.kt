package com.cas.musicplayer.ui.artists.artistdetail.videos

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.cas.musicplayer.R
import com.cas.musicplayer.base.SimpleBaseAdapter
import com.cas.musicplayer.base.SimpleBaseViewHolder
import com.cas.musicplayer.data.enteties.MusicTrack
import com.cas.musicplayer.data.models.Artist
import com.cas.musicplayer.player.PlayerQueue
import com.cas.musicplayer.ui.home.ui.model.DisplayedVideoItem
import com.cas.musicplayer.utils.loadImage
import kotlinx.android.synthetic.main.item_artist.view.*

/**
 **********************************
 * Created by Abdelhadi on 4/4/19.
 **********************************
 */
class ArtistVideosAdapter(
    private val artist: Artist,
    private val onVideoSelected: () -> Unit,
    private val onClickMore: ((track: MusicTrack) -> Unit)
) : SimpleBaseAdapter<DisplayedVideoItem, ArtistVideosViewHolder>() {

    override val cellResId: Int = R.layout.item_artist
    override fun createViewHolder(view: View): ArtistVideosViewHolder {
        return ArtistVideosViewHolder(view, artist, dataItems, onVideoSelected, onClickMore)
    }
}

class ArtistVideosViewHolder(
    view: View,
    private val artist: Artist,
    private val items: List<DisplayedVideoItem>,
    private val onVideoSelected: () -> Unit,
    private val onClickMore: ((track: MusicTrack) -> Unit)
) : SimpleBaseViewHolder<DisplayedVideoItem>(view) {

    private val imgSong: ImageView = view.findViewById(R.id.imgSong)
    private val txtTitle: TextView = view.findViewById(R.id.txtTitle)
    private val txtDuration: TextView = view.findViewById(R.id.txtDuration)
    private val txtCategory: TextView = view.findViewById(R.id.txtCategory)

    override fun bind(item: DisplayedVideoItem) {
        imgSong.loadImage(item.songImagePath)
        txtTitle.text = item.songTitle
        txtDuration.text = item.songDuration
        txtCategory.text = "${artist.name} - Topic"

        itemView.btnMore.setOnClickListener {
            onClickMore.invoke(item.track)
        }
        itemView.setOnClickListener {
            onVideoSelected()
            val tracks = items.map { it.track }
            PlayerQueue.playTrack(item.track, tracks)
        }
    }
}