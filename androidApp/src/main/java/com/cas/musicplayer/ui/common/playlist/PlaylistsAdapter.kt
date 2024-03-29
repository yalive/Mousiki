package com.cas.musicplayer.ui.common.playlist

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.navigation.findNavController
import com.cas.common.adapter.SimpleBaseAdapter
import com.cas.common.adapter.SimpleBaseViewHolder
import com.cas.common.extensions.onClick
import com.cas.musicplayer.R
import com.mousiki.shared.data.models.Artist
import com.mousiki.shared.domain.models.Playlist
import com.cas.musicplayer.ui.artists.EXTRAS_ARTIST
import com.cas.musicplayer.ui.common.songs.AppImage
import com.cas.musicplayer.ui.common.songs.BaseSongsFragment
import com.cas.musicplayer.ui.playlist.songs.PlaylistSongsFragment
import com.cas.musicplayer.utils.loadImage
import com.cas.musicplayer.utils.navigateSafeAction

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
            itemView.onClick {
                val item = dataItems[adapterPosition]
                val bundle = Bundle()
                bundle.putString(PlaylistSongsFragment.EXTRAS_PLAYLIST_ID, item.id)
                bundle.putString(PlaylistSongsFragment.EXTRAS_PLAYLIST_DESC, "")
                val artist = Artist(item.title, "US", item.id, item.urlImage)
                bundle.putParcelable(EXTRAS_ARTIST, artist)
                bundle.putParcelable(
                    BaseSongsFragment.EXTRAS_ID_FEATURED_IMAGE,
                    AppImage.AppImageUrl(artist.urlImage)
                )
                itemView.findNavController().navigateSafeAction(R.id.playlistVideosFragment, bundle)
            }
        }

        override fun bind(item: Playlist) {
            imgSong.loadImage(item.urlImage)
            txtTitle.text = item.title
            txtCount.text = "${item.itemCount}"
        }
    }
}