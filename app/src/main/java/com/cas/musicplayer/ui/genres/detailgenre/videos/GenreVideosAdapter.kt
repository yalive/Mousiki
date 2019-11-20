package com.cas.musicplayer.ui.genres.detailgenre.videos

import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import com.cas.musicplayer.R
import com.cas.musicplayer.base.SimpleBaseAdapter
import com.cas.musicplayer.base.SimpleBaseViewHolder
import com.cas.musicplayer.data.enteties.MusicTrack
import com.cas.musicplayer.player.PlayerQueue
import com.cas.musicplayer.ui.home.domain.model.GenreMusic
import com.cas.musicplayer.ui.home.ui.model.DisplayedVideoItem
import com.cas.musicplayer.utils.loadImage
import com.cas.musicplayer.utils.observer

/**
 **********************************
 * Created by Abdelhadi on 4/4/19.
 **********************************
 */
class GenreVideosAdapter(
    private val genreMusic: GenreMusic,
    private val onVideoSelected: () -> Unit,
    private val onClickMore: ((track: MusicTrack) -> Unit)
) : SimpleBaseAdapter<DisplayedVideoItem, GenreVideosViewHolder>() {

    override val cellResId: Int = R.layout.item_artist

    override fun createViewHolder(view: View): GenreVideosViewHolder = GenreVideosViewHolder(
        view = view,
        items = dataItems,
        genreMusic = genreMusic,
        onClickMore = onClickMore,
        onVideoSelected = onVideoSelected
    )
}

class GenreVideosViewHolder(
    view: View,
    val items: List<DisplayedVideoItem>,
    val genreMusic: GenreMusic,
    val onClickMore: ((track: MusicTrack) -> Unit),
    val onVideoSelected: () -> Unit
) : SimpleBaseViewHolder<DisplayedVideoItem>(view) {

    private val imgSong: ImageView = view.findViewById(R.id.imgSong)
    private val txtTitle: TextView = view.findViewById(R.id.txtTitle)
    private val txtDuration: TextView = view.findViewById(R.id.txtDuration)
    private val txtCategory: TextView = view.findViewById(R.id.txtCategory)
    private val btnMore: ImageButton = view.findViewById(R.id.btnMore)

    override fun bind(item: DisplayedVideoItem) {
        imgSong.loadImage(item.songImagePath)
        txtTitle.text = item.songTitle
        txtDuration.text = item.songDuration
        txtCategory.text = "${genreMusic.title} - Topic"

        btnMore.setOnClickListener {
            onClickMore.invoke(item.track)
        }
        itemView.setOnClickListener {
            onVideoSelected()
            val tracks = items.map { it.track }
            PlayerQueue.playTrack(item.track, tracks)
        }
    }
}