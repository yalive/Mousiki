package com.cas.musicplayer.ui.local.folders

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.cas.musicplayer.R
import com.cas.musicplayer.databinding.ItemLocalFolderBinding
import com.cas.musicplayer.utils.Utils.getAlbumArtUri
import com.cas.musicplayer.utils.dpToPixel
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.squareup.picasso.Picasso


class FoldersAdapter :
    ListAdapter<Folder, FoldersAdapter.ViewHolder>(FoldersDiffCallback()) {

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val from = LayoutInflater.from(viewGroup.context)
        val binding = ItemLocalFolderBinding.inflate(from, viewGroup, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val folder = getItem(position)
        viewHolder.bind(folder = folder)
    }

    inner class ViewHolder(val binding: ItemLocalFolderBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(folder: Folder) {
            binding.folderName.text = folder.name
            binding.txtFolderPath.text = folder.path
            try {
                val imageSize = itemView.context.dpToPixel(55f)
                Picasso.get()
                    .load(getAlbumArtUri(123))
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

class FoldersDiffCallback : DiffUtil.ItemCallback<Folder>() {
    override fun areItemsTheSame(oldItem: Folder, newItem: Folder): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: Folder, newItem: Folder): Boolean {
        return oldItem == newItem
    }
}