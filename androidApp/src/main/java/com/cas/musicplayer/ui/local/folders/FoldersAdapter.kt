package com.cas.musicplayer.ui.local.folders

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.findFragment
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.cas.common.extensions.onClick
import com.cas.musicplayer.R
import com.cas.musicplayer.databinding.ItemLocalFolderBinding
import com.cas.musicplayer.ui.local.folders.options.FolderOption
import com.cas.musicplayer.ui.local.folders.options.FolderOptionsFragment


class FoldersAdapter(
    private val onOption: (Folder, FolderOption) -> Unit
) : ListAdapter<Folder, FoldersAdapter.ViewHolder>(FoldersDiffCallback()) {

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
            binding.txtFolderPath.text = folder.shortPath
            binding.txtSongsCount.text = "${folder.ids.count()}"
            binding.btnMore.onClick {
                val fm = itemView.findFragment<Fragment>().childFragmentManager
                FolderOptionsFragment.present(fm, folder) {
                    onOption(folder, it)
                }
            }
            itemView.onClick {
                itemView.findNavController().navigate(
                    R.id.action_localSongsContainerFragment_to_folderDetailsFragment,
                    bundleOf(
                        FolderDetailsFragment.EXTRAS_FOLDER_PATH to folder.path,
                        FolderDetailsFragment.EXTRAS_FOLDER_NAME to folder.name,
                        FolderVideoDetailsFragment.EXTRAS_FOLDER to folder
                    )
                )
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