package com.cas.musicplayer.ui.searchyoutube.playlists

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.cas.musicplayer.R
import com.cas.musicplayer.data.enteties.Playlist
import com.cas.musicplayer.data.models.Artist
import com.cas.musicplayer.ui.artists.artistdetail.ArtistFragment
import com.cas.musicplayer.ui.playlistvideos.PlaylistVideosFragment
import com.cas.musicplayer.utils.loadImage

/**
 * Created by Fayssel Yabahddou on 4/13/19.
 */
class YTSearchPlaylistsAdapter(
    items: List<Playlist>
) :
    RecyclerView.Adapter<YTSearchPlaylistsAdapter.YTSearchPlaylistsViewHolder>() {

    var items: List<Playlist> = items
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): YTSearchPlaylistsViewHolder {

        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_channel_playlist, parent, false)
        return YTSearchPlaylistsViewHolder(view)
    }

    override fun getItemCount(): Int {

        return items.size
    }

    override fun onBindViewHolder(holder: YTSearchPlaylistsViewHolder, position: Int) {
        holder.bind(items.get(position))
    }


    inner class YTSearchPlaylistsViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        private val imgSong: ImageView = view.findViewById(R.id.imgSong)
        private val txtTitle: TextView = view.findViewById(R.id.txtTitle)
        private val txtCategory: TextView = view.findViewById(R.id.txtCategory)
        private val txtCount: TextView = view.findViewById(R.id.txtCount)

        init {
            itemView.setOnClickListener {
                val item = items[adapterPosition]
                val bundle = Bundle()
                bundle.putString(PlaylistVideosFragment.EXTRAS_PLAYLIST_ID, item.id)
                val artist = Artist(item.title, "US", item.id, item.urlImage)
                bundle.putParcelable(ArtistFragment.EXTRAS_ARTIST, artist)
                itemView.findNavController().navigate(R.id.playlistVideosFragment, bundle)
            }
        }

        fun bind(item: Playlist) {
            imgSong.loadImage(item.urlImage)
            txtTitle.text = item.title
            txtCount.text = "${item.itemCount}"
        }
    }
}