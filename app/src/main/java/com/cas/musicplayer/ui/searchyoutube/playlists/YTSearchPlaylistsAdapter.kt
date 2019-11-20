package com.cas.musicplayer.ui.searchyoutube.playlists

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.navigation.findNavController
import com.cas.musicplayer.R
import com.cas.musicplayer.base.SimpleBaseAdapter
import com.cas.musicplayer.base.SimpleBaseViewHolder
import com.cas.musicplayer.data.enteties.Playlist
import com.cas.musicplayer.data.models.Artist
import com.cas.musicplayer.ui.artists.artistdetail.ArtistFragment
import com.cas.musicplayer.ui.playlistvideos.PlaylistVideosFragment
import com.cas.musicplayer.utils.loadImage

/**
 * Created by Fayssel Yabahddou on 4/13/19.
 */
class YTSearchPlaylistsAdapter :
    SimpleBaseAdapter<Playlist, YTSearchPlaylistsAdapter.YTSearchPlaylistsViewHolder>() {
    override val cellResId: Int = R.layout.item_channel_playlist
    override fun createViewHolder(view: View): YTSearchPlaylistsViewHolder {
        return YTSearchPlaylistsViewHolder(view)
    }

    inner class YTSearchPlaylistsViewHolder(view: View) : SimpleBaseViewHolder<Playlist>(view) {

        private val imgSong: ImageView = view.findViewById(R.id.imgSong)
        private val txtTitle: TextView = view.findViewById(R.id.txtTitle)
        private val txtCount: TextView = view.findViewById(R.id.txtCount)

        init {
            itemView.setOnClickListener {
                val item = dataItems[adapterPosition]
                val bundle = Bundle()
                bundle.putString(PlaylistVideosFragment.EXTRAS_PLAYLIST_ID, item.id)
                val artist = Artist(item.title, "US", item.id, item.urlImage)
                bundle.putParcelable(ArtistFragment.EXTRAS_ARTIST, artist)
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