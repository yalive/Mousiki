package com.cas.musicplayer.ui.local.songs

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.cas.musicplayer.R
import com.cas.musicplayer.databinding.ItemLocalSongBinding
import com.cas.musicplayer.utils.Utils.getAlbumArtUri
import com.cas.musicplayer.utils.dpToPixel
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.mousiki.shared.domain.models.Song
import com.squareup.picasso.Picasso


class LocalSongsAdapter :
    ListAdapter<Song, LocalSongsAdapter.ViewHolder>(LocalSongsDiffCallback()) {

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val from = LayoutInflater.from(viewGroup.context)
        val binding = ItemLocalSongBinding.inflate(from, viewGroup, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val song = getItem(position)
        viewHolder.bind(song = song)
    }

    inner class ViewHolder(val binding: ItemLocalSongBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(song: Song) {
            binding.txtTitle.text = song.title
            binding.txtArtist.text = song.artistName
            try {
                val imageSize = itemView.context.dpToPixel(55f)
                Picasso.get()
                    .load(getAlbumArtUri(song.albumId))
                    .placeholder(R.drawable.ic_music_note)
                    .resize(imageSize, imageSize)
                    .into(binding.imgSong)
            } catch (e: Exception) {
                FirebaseCrashlytics.getInstance().recordException(e)
            } catch (e: OutOfMemoryError) {
                FirebaseCrashlytics.getInstance().recordException(e)
            }
        }
    }
}

class LocalSongsDiffCallback : DiffUtil.ItemCallback<Song>() {
    override fun areItemsTheSame(oldItem: Song, newItem: Song): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: Song, newItem: Song): Boolean {
        return oldItem == newItem
    }
}