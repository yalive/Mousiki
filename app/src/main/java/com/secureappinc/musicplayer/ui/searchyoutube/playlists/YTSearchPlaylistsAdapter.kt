package com.secureappinc.musicplayer.ui.searchyoutube.playlists

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.secureappinc.musicplayer.R
import com.secureappinc.musicplayer.models.Artist
import com.secureappinc.musicplayer.models.YTTrendingItem
import com.secureappinc.musicplayer.ui.artistdetail.ArtistFragment
import com.secureappinc.musicplayer.ui.artistdetail.detailplaylist.PlaylistVideosFragment
import com.secureappinc.musicplayer.utils.loadImage

/**
 * Created by Fayssel Yabahddou on 4/13/19.
 */
class YTSearchPlaylistsAdapter(
    items: List<YTTrendingItem>
) :
    RecyclerView.Adapter<YTSearchPlaylistsAdapter.NewReleaseViewHolder>() {

    var items: List<YTTrendingItem> = items
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewReleaseViewHolder {

        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_channel_playlist, parent, false)
        return NewReleaseViewHolder(view)
    }

    override fun getItemCount(): Int {

        return items.size
    }

    override fun onBindViewHolder(holder: NewReleaseViewHolder, position: Int) {
        holder.bind(items.get(position))
    }


    inner class NewReleaseViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        private val imgSong: ImageView = view.findViewById(R.id.imgSong)
        private val txtTitle: TextView = view.findViewById(R.id.txtTitle)
        private val txtCategory: TextView = view.findViewById(R.id.txtCategory)
        private val txtCount: TextView = view.findViewById(R.id.txtCount)

        init {
            itemView.setOnClickListener {
                val item = items[adapterPosition]
                val bundle = Bundle()
                bundle.putString(PlaylistVideosFragment.EXTRAS_PLAYLIST_ID, item.id)
                val artist = Artist(item.snippetTitle(), "US", item.id, item.snippet?.urlImageOrEmpty())
                bundle.putParcelable(ArtistFragment.EXTRAS_ARTIST, artist)
                itemView.findNavController().navigate(R.id.playlistVideosFragment, bundle)
            }
        }

        fun bind(item: YTTrendingItem) {
            imgSong.loadImage(item.snippet)
            txtTitle.text = item.snippetTitle()
            //txtCategory.text = "${artist.name}"
            txtCount.text = "${item.contentDetails.itemCount}"
        }
    }
}