package com.cas.musicplayer.ui.local.albums

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.cas.common.extensions.onClick
import com.cas.musicplayer.R
import com.cas.musicplayer.databinding.ItemLocalAlbumBinding
import com.cas.musicplayer.utils.Utils.getAlbumArtUri
import com.cas.musicplayer.utils.dpToPixel
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.mousiki.shared.domain.models.Album
import com.squareup.picasso.Picasso


class AlbumsAdapter :
    ListAdapter<Album, AlbumsAdapter.ViewHolder>(AlbumsDiffCallback()) {

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val from = LayoutInflater.from(viewGroup.context)
        val binding = ItemLocalAlbumBinding.inflate(from, viewGroup, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val album = getItem(position)
        viewHolder.bind(album = album)
    }

    inner class ViewHolder(val binding: ItemLocalAlbumBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(album: Album) {
            binding.txtAlbumTitle.text = album.title
            binding.txtSongsCount.text = itemView.context.resources.getQuantityString(
                R.plurals.playlist_tracks_counts,
                album.songCount,
                album.songCount
            )
            try {
                val imageSize = itemView.context.dpToPixel(55f)
                Picasso.get()
                    .load(getAlbumArtUri(album.id))
                    .placeholder(R.drawable.ic_music_note)
                    .resize(imageSize, imageSize)
                    .into(binding.imgAlbum)
            } catch (e: Exception) {
                FirebaseCrashlytics.getInstance().recordException(e)
            } catch (e: OutOfMemoryError) {
                FirebaseCrashlytics.getInstance().recordException(e)
            }

            itemView.onClick {
                itemView.findNavController().navigate(
                    R.id.action_localSongsContainerFragment_to_albumDetailsFragment,
                    bundleOf(AlbumDetailsFragment.EXTRAS_ALBUM_ID to album.id)
                )
            }
        }
    }
}

class AlbumsDiffCallback : DiffUtil.ItemCallback<Album>() {
    override fun areItemsTheSame(oldItem: Album, newItem: Album): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: Album, newItem: Album): Boolean {
        return oldItem == newItem
    }
}