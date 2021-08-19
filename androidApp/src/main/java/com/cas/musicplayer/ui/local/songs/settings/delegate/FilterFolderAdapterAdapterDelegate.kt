package com.cas.musicplayer.ui.local.songs.settings.delegate

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.cas.common.extensions.onClick
import com.cas.musicplayer.databinding.ItemSettingsFolderBinding
import com.cas.musicplayer.delegateadapter.AdapterDelegate
import com.cas.musicplayer.ui.local.songs.settings.model.FolderUiModel
import com.mousiki.shared.domain.models.DisplayableItem


class FilterFolderAdapterAdapterDelegate : AdapterDelegate<List<DisplayableItem>>() {

    override fun isForViewType(items: List<DisplayableItem>, position: Int): Boolean {
        return items[position] is FolderUiModel
    }

    override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        val from = LayoutInflater.from(parent.context)
        val binding = ItemSettingsFolderBinding.inflate(from, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(
        items: List<DisplayableItem>,
        position: Int,
        holder: RecyclerView.ViewHolder
    ) {
        val viewHolder = holder as ViewHolder
        viewHolder.bind(items[position] as FolderUiModel)
    }

    override fun onBindViewHolder(
        items: List<DisplayableItem>,
        position: Int,
        holder: RecyclerView.ViewHolder,
        payloads: List<Any>
    ) {
        val item = items[position] as FolderUiModel
        val viewHolder = holder as ViewHolder
        if (payloads.isEmpty() || payloads[0] !is Bundle) {
            viewHolder.bind(item)
        } else {
            viewHolder.updateCheckbox(item)
        }
    }

    inner class ViewHolder(val binding: ItemSettingsFolderBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: FolderUiModel) {
            val folder = item.folder
            binding.checkbox.isChecked = item.hidden
            binding.folderName.text = folder.name
            binding.txtFolderPath.text = item.subtitle

            itemView.onClick {
                item.listener(folder)
            }
        }

        fun updateCheckbox(item: FolderUiModel) {
            binding.checkbox.isChecked = item.hidden
        }
    }
}