package com.cas.musicplayer.ui.common.multiselection

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.cas.common.extensions.onClick
import com.cas.musicplayer.databinding.ItemMultiselectTrackBinding
import com.cas.musicplayer.utils.loadTrackImage

class MultiSelectTracksAdapter(
    private val viewModel: MultiSelectTracksViewModel
) : ListAdapter<SelectableTrack, MultiSelectTracksAdapter.ViewHolder>(SelectableTrackDiffCallback()) {

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val from = LayoutInflater.from(viewGroup.context)
        val binding = ItemMultiselectTrackBinding.inflate(from, viewGroup, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val track = getItem(position)
        viewHolder.bind(track)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int, payloads: MutableList<Any>) {
        val item = getItem(position)
        if (payloads.isEmpty() || payloads[0] !is Bundle) {
            holder.bind(item)
        } else {
            holder.binding.root.isSelected = item.selected
        }
    }

    inner class ViewHolder(val binding: ItemMultiselectTrackBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: SelectableTrack) {
            binding.txtTitle.text = item.track.title
            binding.txtCategory.text = item.track.artistName
            binding.checkbox.isChecked = item.selected
            binding.checkbox.isVisible = false
            binding.imgSong.loadTrackImage(item.track)
            itemView.onClick {
                viewModel.onClickTrack(item)
            }

            binding.root.isSelected = item.selected
        }
    }
}

class SelectableTrackDiffCallback : DiffUtil.ItemCallback<SelectableTrack>() {

    override fun getChangePayload(oldItem: SelectableTrack, newItem: SelectableTrack): Any? {
        if (oldItem.track == newItem.track) {
            return bundleOf()
        }
        return super.getChangePayload(oldItem, newItem)
    }

    override fun areItemsTheSame(oldItem: SelectableTrack, newItem: SelectableTrack): Boolean {
        return oldItem.track.id == newItem.track.id
    }

    override fun areContentsTheSame(oldItem: SelectableTrack, newItem: SelectableTrack): Boolean {
        return oldItem == newItem
    }
}
