package com.cas.musicplayer.ui.local.songs.settings.delegate

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.cas.musicplayer.databinding.ItemSettingsFilterSongsBinding
import com.cas.musicplayer.delegateadapter.AdapterDelegate
import com.mousiki.shared.domain.models.DisplayableItem

class FilterAudioSettingsAdapterDelegate : AdapterDelegate<List<DisplayableItem>>() {

    override fun isForViewType(items: List<DisplayableItem>, position: Int): Boolean {
        return items[position] is FilterAudioSettingsItem
    }

    override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        val from = LayoutInflater.from(parent.context)
        val binding = ItemSettingsFilterSongsBinding.inflate(from, parent, false)
        return ViewHolder(binding)
    }


    override fun onBindViewHolder(
        items: List<DisplayableItem>,
        position: Int,
        holder: RecyclerView.ViewHolder
    ) {
        val viewHolder = holder as ViewHolder
        viewHolder.bind(items[position] as FilterAudioSettingsItem)
    }

    override fun onBindViewHolder(
        items: List<DisplayableItem>,
        position: Int,
        holder: RecyclerView.ViewHolder,
        payloads: List<Any>
    ) {
        val item = items[position] as FilterAudioSettingsItem
        val viewHolder = holder as ViewHolder
        if (payloads.isEmpty() || payloads[0] !is Bundle) {
            viewHolder.bind(item)
        } else {
            viewHolder.updateSwitches(item)
        }
    }

    inner class ViewHolder(val binding: ItemSettingsFilterSongsBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: FilterAudioSettingsItem) {
            binding.checkboxFilterPerSize.isChecked = item.filterLessThanSize
            binding.checkboxFilterPerDuration.isChecked = item.filterLessDuration

            binding.checkboxFilterPerDuration.setOnCheckedChangeListener { buttonView, isChecked ->
                item.toggleMinDuration()
            }

            binding.checkboxFilterPerSize.setOnCheckedChangeListener { buttonView, isChecked ->
                item.toggleMinSize()
            }
        }


        fun updateSwitches(item: FilterAudioSettingsItem) {
            binding.checkboxFilterPerSize.isChecked = item.filterLessThanSize
            binding.checkboxFilterPerDuration.isChecked = item.filterLessDuration
        }
    }
}

data class FilterAudioSettingsItem(
    val filterLessDuration: Boolean,
    val filterLessThanSize: Boolean,
    val toggleMinDuration: () -> Unit,
    val toggleMinSize: () -> Unit,
) : DisplayableItem