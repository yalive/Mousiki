package com.cas.musicplayer.ui.genres.detailgenre.playlists

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.cas.musicplayer.R
import com.cas.common.adapter.SimpleBaseAdapter
import com.cas.common.adapter.SimpleBaseViewHolder
import com.cas.musicplayer.domain.model.Playlist
import com.cas.musicplayer.domain.model.GenreMusic
import com.cas.musicplayer.utils.loadImage

/**
 **********************************
 * Created by Abdelhadi on 4/4/19.
 **********************************
 */
class GenrePlaylistsAdapter(
    private val genreMusic: GenreMusic
) : SimpleBaseAdapter<Playlist, PlaylistsViewHolder>() {

    override val cellResId: Int = R.layout.item_channel_playlist

    override fun createViewHolder(view: View): PlaylistsViewHolder {
        return PlaylistsViewHolder(view, genreMusic)
    }
}

class PlaylistsViewHolder(
    view: View,
    private val genreMusic: GenreMusic
) : SimpleBaseViewHolder<Playlist>(view) {

    private val imgSong: ImageView = view.findViewById(R.id.imgSong)
    private val txtTitle: TextView = view.findViewById(R.id.txtTitle)
    private val txtCategory: TextView = view.findViewById(R.id.txtCategory)
    private val txtCount: TextView = view.findViewById(R.id.txtCount)

    override fun bind(item: Playlist) {
        imgSong.loadImage(item.urlImage)
        txtTitle.text = item.title
        txtCategory.text = "${genreMusic.title}"
        txtCount.text = "${item.itemCount}"
    }
}
