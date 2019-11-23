package com.cas.musicplayer.ui.artists.artistdetail.playlists

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.cas.musicplayer.R
import com.cas.common.adapter.SimpleBaseAdapter
import com.cas.common.adapter.SimpleBaseViewHolder
import com.cas.musicplayer.domain.model.Playlist
import com.cas.musicplayer.data.remote.models.Artist
import com.cas.musicplayer.utils.loadImage

/**
 **********************************
 * Created by Abdelhadi on 4/4/19.
 **********************************
 */
class ArtistPlaylistsAdapter(
    private val artist: Artist,
    private val onClickPlaylist: (playlist: Playlist) -> Unit
) : SimpleBaseAdapter<Playlist, ArtistPlaylistViewHolder>() {
    override val cellResId: Int = R.layout.item_channel_playlist
    override fun createViewHolder(view: View): ArtistPlaylistViewHolder {
        return ArtistPlaylistViewHolder(view, artist, onClickPlaylist)
    }
}

class ArtistPlaylistViewHolder(
    view: View,
    private val artist: Artist,
    private val onClickPlaylist: (playlist: Playlist) -> Unit
) : SimpleBaseViewHolder<Playlist>(view) {

    private val imgSong: ImageView = view.findViewById(R.id.imgSong)
    private val txtTitle: TextView = view.findViewById(R.id.txtTitle)
    private val txtCategory: TextView = view.findViewById(R.id.txtCategory)
    private val txtCount: TextView = view.findViewById(R.id.txtCount)

    override fun bind(item: Playlist) {
        imgSong.loadImage(item.urlImage)
        txtTitle.text = item.title
        txtCategory.text = "${artist.name}"
        txtCount.text = "${item.itemCount}"
        itemView.setOnClickListener {
            onClickPlaylist(item)
        }
    }
}