package com.cas.musicplayer.ui.local.artists

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.cas.musicplayer.R
import com.cas.musicplayer.databinding.ItemLocalArtistBinding
import com.cas.musicplayer.ui.local.artists.model.LocalArtist
import com.cas.musicplayer.utils.Utils.getAlbumArtUri
import com.cas.musicplayer.utils.dpToPixel
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.squareup.picasso.Picasso


class LocalArtistsAdapter :
    ListAdapter<LocalArtist, LocalArtistsAdapter.ViewHolder>(LocalArtistsDiffCallback()) {

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val from = LayoutInflater.from(viewGroup.context)
        val binding = ItemLocalArtistBinding.inflate(from, viewGroup, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val artist = getItem(position)
        viewHolder.bind(artist = artist)
    }

    inner class ViewHolder(val binding: ItemLocalArtistBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(artist: LocalArtist) {
            binding.artistName.text = artist.name
            binding.txtSongsCount.text = itemView.context.resources.getQuantityString(
                R.plurals.playlist_tracks_counts,
                artist.songCount,
                artist.songCount
            )
            try {
                val imageSize = itemView.context.dpToPixel(55f)
                Picasso.get()
                    .load(getAlbumArtUri(artist.id))
                    .placeholder(R.drawable.ic_music_note)
                    .resize(imageSize, imageSize)
                    .into(binding.imgAlbum)
            } catch (e: Exception) {
                FirebaseCrashlytics.getInstance().recordException(e)
            } catch (e: OutOfMemoryError) {
                FirebaseCrashlytics.getInstance().recordException(e)
            }
        }
    }
}

class LocalArtistsDiffCallback : DiffUtil.ItemCallback<LocalArtist>() {
    override fun areItemsTheSame(oldItem: LocalArtist, newItem: LocalArtist): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: LocalArtist, newItem: LocalArtist): Boolean {
        return oldItem == newItem
    }
}