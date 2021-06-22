package com.cas.musicplayer.ui.local.playlists

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.cas.musicplayer.R
import com.cas.musicplayer.databinding.ItemLocalPlaylistBinding
import com.mousiki.shared.domain.models.Playlist
import com.squareup.picasso.Picasso


class LocalPlaylistsAdapter :
    ListAdapter<Playlist, LocalPlaylistsAdapter.ViewHolder>(LocalSongsDiffCallback()) {

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val from = LayoutInflater.from(viewGroup.context)
        val binding = ItemLocalPlaylistBinding.inflate(from, viewGroup, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val playlist = getItem(position)
        viewHolder.bind(playlist, position)
    }

    inner class ViewHolder(val binding: ItemLocalPlaylistBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(playlist: Playlist, position: Int) {
            val context = itemView.context

            binding.playlistName.text = playlist.title
            binding.songsCount.text = context.resources.getQuantityString(
                R.plurals.playlist_tracks_counts,
                playlist.itemCount,
                playlist.itemCount
            )


            val drawable = when (position) {
                0 -> ContextCompat.getDrawable(context, R.drawable.fav_playlist)
                1 -> ContextCompat.getDrawable(context, R.drawable.most_played_playlist)
                2 -> ContextCompat.getDrawable(context, R.drawable.recently_played_playlist)

                else -> return
            }

            val urlImage = if (playlist.urlImage.isNotEmpty()) playlist.urlImage else null

            drawable?.let {
                Picasso.get()
                    .load(urlImage)
                    .placeholder(it)
                    .into(binding.imagePlaylist)
            }

        }
    }

}

class LocalSongsDiffCallback : DiffUtil.ItemCallback<Playlist>() {
    override fun areItemsTheSame(oldItem: Playlist, newItem: Playlist): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: Playlist, newItem: Playlist): Boolean {
        return oldItem == newItem
    }
}