package com.cas.musicplayer.ui.local.songs

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.cas.common.extensions.onClick
import com.cas.musicplayer.R
import com.cas.musicplayer.databinding.ItemSongsActionsHeaderBinding
import com.cas.musicplayer.delegateadapter.AdapterDelegate
import com.mousiki.shared.domain.models.DisplayableItem

class HeaderSongsActionsAdapterDelegate(
    private val onSortClicked: () -> Unit,
    private val onFilterClicked: () -> Unit,
    private val showCountsAndSortButton: Boolean,
    private val showFilter: Boolean,
) : AdapterDelegate<List<DisplayableItem>>() {

    override fun isForViewType(items: List<DisplayableItem>, position: Int): Boolean {
        return items[position] is HeaderSongsActionsItem
    }

    override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        val from = LayoutInflater.from(parent.context)
        val binding = ItemSongsActionsHeaderBinding.inflate(from, parent, false)
        return ViewHolder(binding)
    }


    override fun onBindViewHolder(
        items: List<DisplayableItem>,
        position: Int,
        holder: RecyclerView.ViewHolder
    ) {
        val viewHolder = holder as ViewHolder
        viewHolder.bind(items[position] as HeaderSongsActionsItem)
    }

    inner class ViewHolder(val binding: ItemSongsActionsHeaderBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(header: HeaderSongsActionsItem) {
            val songsCount = binding.songsCount
            val sortSong = binding.sortSong
            val filterSongs = binding.filterSongs

            filterSongs.isVisible = showFilter
            songsCount.isVisible = showCountsAndSortButton
            sortSong.isVisible = showCountsAndSortButton

            songsCount.text = itemView.context.resources.getQuantityString(
                R.plurals.numberOfSongs,
                header.songsCount,
                header.songsCount
            )

            sortSong.onClick {
                onSortClicked()
            }

            binding.playAllSong.onClick {
                header.onPlayAllTracks()
            }

            binding.shuffleSong.onClick {
                header.onShuffleAllTracks()
            }

            binding.filterSongs.onClick {
                onFilterClicked()
            }

            binding.btnMultiSelect.onClick {
                header.onMultiSelectTracks()
            }
        }
    }
}

data class HeaderSongsActionsItem(
    val songsCount: Int,
    val onPlayAllTracks: () -> Unit,
    val onShuffleAllTracks: () -> Unit,
    val onMultiSelectTracks: () -> Unit,
) : DisplayableItem