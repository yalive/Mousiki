package com.cas.musicplayer.ui.common.playlist

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.navigation.findNavController
import com.cas.common.adapter.SimpleBaseAdapter
import com.cas.common.adapter.SimpleBaseViewHolder
import com.cas.musicplayer.R
import com.cas.musicplayer.data.remote.models.Artist
import com.cas.musicplayer.domain.model.Playlist
import com.cas.musicplayer.ui.artists.EXTRAS_ARTIST
import com.cas.musicplayer.ui.playlistvideos.PlaylistSongsFragment
import com.cas.musicplayer.utils.loadImage

/**
 ***************************************
 * Created by Abdelhadi on 2019-12-07.
 ***************************************
 */
class PlaylistsAdapter : SimpleBaseAdapter<Playlist, PlaylistsAdapter.ViewHolder>() {

    override val cellResId: Int = R.layout.item_channel_playlist
    override fun createViewHolder(view: View): ViewHolder {
        return ViewHolder(view)
    }

    inner class ViewHolder(view: View) : SimpleBaseViewHolder<Playlist>(view) {
        private val imgSong: ImageView = view.findViewById(R.id.imgSong)
        private val txtTitle: TextView = view.findViewById(R.id.txtTitle)
        private val txtCount: TextView = view.findViewById(R.id.txtCount)

        init {
            itemView.setOnClickListener {
                val item = dataItems[adapterPosition]
                val bundle = Bundle()
                bundle.putString(PlaylistSongsFragment.EXTRAS_PLAYLIST_ID, item.id)
                val artist = Artist(item.title, "US", item.id, item.urlImage)
                bundle.putParcelable(EXTRAS_ARTIST, artist)
                itemView.findNavController().navigate(R.id.playlistVideosFragment, bundle)
            }
        }

        override fun bind(item: Playlist) {
            imgSong.loadImage(item.urlImage)
            txtTitle.text = item.title
            txtCount.text = "${item.itemCount}"
        }
    }
}